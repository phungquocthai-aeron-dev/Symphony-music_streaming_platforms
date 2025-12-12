import os
import pickle
import numpy as np
from utils import extract_features
from fastdtw import fastdtw
from scipy.spatial.distance import euclidean, cosine, cityblock
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

MODEL_PATH = "models/embeddings.pkl"

def compute_shape_similarity(input_seq, song_seq):
    try:
        input_flat = input_seq.flatten()
        song_flat = song_seq.flatten()
        
        if len(input_flat) == 0 or len(song_flat) == 0:
            return float("inf")
        
        # 1. DTW distance
        try:
            dtw_distance, _ = fastdtw(input_seq, song_seq, dist=euclidean)
            dtw_normalized = dtw_distance / min(len(input_seq), len(song_seq))
        except:
            dtw_normalized = 1.0
        
        # 2. Độ tương tự theo góc Cosin
        min_len = min(len(input_flat), len(song_flat))
        input_truncated = input_flat[:min_len] 
        song_truncated = song_flat[:min_len]
        
        cosine_dist = cosine(input_truncated, song_truncated)
        if np.isnan(cosine_dist) or np.isinf(cosine_dist):
            cosine_dist = 1.0
        
        # 3. Correlation coefficient 
        try:
            correlation = np.corrcoef(input_truncated, song_truncated)[0, 1]
            if np.isnan(correlation):
                correlation = 0
            correlation_dist = 1 - abs(correlation)
        except:
            correlation_dist = 1.0
        
        # 4. Manhattan distance
        manhattan_dist = cityblock(input_truncated, song_truncated) / min_len
        manhattan_normalized = min(manhattan_dist / 10.0, 1.0)  # Cap at 1.0
        
        # 5. Direction changes similarity
        try:
            input_diff = np.diff(input_flat)
            song_diff = np.diff(song_flat)
            
            min_diff_len = min(len(input_diff), len(song_diff))
            if min_diff_len > 1:

                input_signs = np.sign(input_diff[:min_diff_len])
                song_signs = np.sign(song_diff[:min_diff_len])
                
                sign_agreement = np.mean(input_signs == song_signs)
                sign_dist = 1 - sign_agreement
            else:
                sign_dist = 1.0
        except:
            sign_dist = 1.0
        
        # 6. Local pattern similarity
        try:
            window_size = min(5, min_len // 3)
            if window_size >= 2:
                local_similarities = []
                for i in range(0, min_len - window_size + 1, window_size):
                    input_window = input_truncated[i:i+window_size]
                    song_window = song_truncated[i:i+window_size]
                    
                    window_corr = np.corrcoef(input_window, song_window)[0, 1]
                    if not np.isnan(window_corr):
                        local_similarities.append(abs(window_corr))
                
                if local_similarities:
                    local_pattern_score = np.mean(local_similarities)
                    local_pattern_dist = 1 - local_pattern_score
                else:
                    local_pattern_dist = 1.0
            else:
                local_pattern_dist = 1.0
        except:
            local_pattern_dist = 1.0
        
        combined_distance = (
            0.25 * dtw_normalized +         
            0.20 * cosine_dist +          
            0.20 * correlation_dist +     
            0.15 * manhattan_normalized + 
            0.10 * sign_dist +          
            0.10 * local_pattern_dist     
        )
        
        noise = np.random.uniform(-0.0001, 0.0001)
        combined_distance += noise
        
        return max(0.0, combined_distance)
        
    except Exception as e:
        logger.warning(f"Error computing similarity: {e}")
        return float("inf")

def predict(audio_path, top_k=5):
    """Enhanced prediction with improved discrimination"""
    try:
        if not os.path.exists(MODEL_PATH):
            raise RuntimeError(f"Embeddings file not found: {MODEL_PATH}")
            
        with open(MODEL_PATH, "rb") as f:
            embeddings = pickle.load(f)
            
        if not embeddings:
            raise RuntimeError("Embeddings database is empty")
        
        logger.info(f"Processing query: {os.path.basename(audio_path)}")
        logger.info(f"Database contains {len(embeddings)} songs")
        
        for i, (name, data) in enumerate(list(embeddings.items())[:3]):
            logger.info(f"  Sample {i+1}: {name} -> shape: {data.shape}")
        
        input_contour = extract_features(audio_path)
        
        if input_contour.ndim != 2:
            raise ValueError("Input contour must be a 2D array (n_features, n_frames)")
        
        input_seq = input_contour.T
        
        results = []
        total_songs = len(embeddings)
        
        for idx, (song_name, song_contour) in enumerate(embeddings.items(), 1):
            if idx % 50 == 0:
                print(f"  Processing {idx}/{total_songs} songs...")
            
            try:
                if song_contour.ndim == 3:
                    song_contour = song_contour[0]
                    logger.info(f"Converted 3D shape for {song_name}: {song_contour.shape}")
                
                if song_contour.ndim != 2:
                    logger.warning(f"Skipping {song_name}: Invalid shape {song_contour.shape}")
                    continue
                
                song_seq = song_contour.T
                
            except Exception as e:
                logger.warning(f"Error processing {song_name}: {e}")
                continue
            
            if len(input_seq) == 0 or len(song_seq) == 0:
                distance = float("inf")
            else:
                distance = compute_shape_similarity(input_seq, song_seq)
            
            results.append({
                'song': song_name, 
                'distance': float(distance)
            })
        
        results.sort(key=lambda x: x['distance'])
        
        valid_results = [r for r in results if r['distance'] != float("inf")]
        if valid_results:
            distances = [r['distance'] for r in valid_results]
            min_dist = min(distances)
            max_dist = max(distances)
            
            for result in results:
                if result['distance'] != float("inf"):
                    if max_dist > min_dist:
                        normalized_dist = (result['distance'] - min_dist) / (max_dist - min_dist)
                        confidence = 100 * np.exp(-3 * normalized_dist) 
                    else:
                        confidence = 100.0
                else:
                    confidence = 0.0
                
                result['confidence'] = round(max(0, confidence), 1)
        
        return results
        
    except Exception as e:
        logger.error(f"Prediction failed: {e}")
        raise

if __name__ == "__main__":
    import sys
    
    if len(sys.argv) != 2:
        print("Usage: python predict.py <audio_file>")
        print("Example: python predict.py test/phaohong.mp3")
        sys.exit(1)
    
    audio_file = sys.argv[1]
    
    if not os.path.exists(audio_file):
        print(f"File not found: {audio_file}")
        sys.exit(1)
    
    print(f"\nSearching for melodies similar to: {audio_file}")
    print("=" * 60)
    
    try:
        results = predict(audio_file, top_k=5)
        
        if results:
            print(f"\nTop 5 matches:")
            print("-" * 60)
            
            for i, result in enumerate(results[:5], 1):
                confidence = result.get('confidence', 0)
                distance = result['distance']
                
                print(f"{i}. {result['song']}")
                print(f"   Distance: {distance:.4f} | Confidence: {confidence}%")
                
                if distance < 0.1:
                    print("   KẾT QUẢ CỰC TỐT!")
                print()
            
            if len(results) > 5:
                print("\n# Remaining matches (for reference):")
                for i, result in enumerate(results[5:], 6):
                    confidence = result.get('confidence', 0)
                    distance = result['distance']
                    print(f"# {i}. {result['song']} - Distance: {distance:.4f} | Confidence: {confidence}%")
            
        else:
            print("No matches found!")
            
    except Exception as e:
        print(f"Error during prediction: {e}")
        sys.exit(1)
