import librosa
import numpy as np
from scipy.signal import savgol_filter, find_peaks, medfilt
from scipy.ndimage import gaussian_filter1d
import warnings
warnings.filterwarnings('ignore')

def load_audio(file_path, sr=16000):
    """Load and preprocess audio with optimizations"""
    try:
        # Giảm thời gian xử lý để tăng tốc
        y, sr = librosa.load(file_path, sr=sr, duration=12, offset=1) 
        
        y = librosa.util.normalize(y)
        y, _ = librosa.effects.trim(y, top_db=25)
        
        # High-pass filter để giảm nhạc cụ
        from scipy.signal import lfilter
        y = lfilter([1, -0.97], [1], y)
        
        return y, sr
    except Exception as e:
        print(f"Error loading {file_path}: {e}")
        return np.array([]), sr

def extract_melody_line(y, sr):
    """Extract F0 with speed and accuracy optimizations"""
    try:
        # Tăng hop_length để xử lý nhanh hơn
        hop_length = 512
        
        # Sử dụng piptrack thay pyin cho tốc độ
        pitches, magnitudes = librosa.piptrack(
            y=y, sr=sr, 
            hop_length=hop_length,
            fmin=75,   # Mở rộng range thấp
            fmax=800,  # Tập trung vào giọng người
            threshold=0.08
        )
        
        # Lấy pitch có magnitude cao nhất tại mỗi frame
        f0 = []
        for t in range(pitches.shape[1]):
            index = magnitudes[:, t].argmax()
            pitch = pitches[index, t]
            f0.append(pitch if pitch > 0 else np.nan)
        
        f0 = np.array(f0)
        voiced_mask = ~np.isnan(f0)
        
        if np.sum(voiced_mask) < 5:
            print("Not enough valid pitches")
            return np.array([]), False
        
        # Median filter để loại bỏ pitch jumps
        f0_clean = medfilt(f0, kernel_size=3)
        
        # Interpolate missing values
        valid_indices = np.where(voiced_mask)[0]
        if len(valid_indices) >= 3:
            f0_interpolated = np.interp(
                np.arange(len(f0)), 
                valid_indices, 
                f0_clean[valid_indices]
            )
            return f0_interpolated, True
        
        return np.array([]), False
        
    except Exception as e:
        print(f"Error extracting melody: {e}")
        return np.array([]), False

def normalize_melody_contour(melody_seq):
    if len(melody_seq) < 5:
        return np.array([])
    
    # Đưa về log scale để khoảng cách nốt phản ánh đúng hơn
    melody_log = np.log2(melody_seq + 1e-8)
    
    # Loại bỏ outlier (IQR)
    p25, p75 = np.percentile(melody_log, [25, 75])
    iqr = p75 - p25
    melody_clipped = np.clip(melody_log, p25 - 1.5 * iqr, p75 + 1.5 * iqr)
    
    # Làm mượt (Gaussian smoothing)
    sigma = max(0.8, len(melody_clipped) / 60)
    melody_smooth = gaussian_filter1d(melody_clipped, sigma=sigma)
    
    # **CHỈ GIỮ HÌNH DẠNG**
    # Dịch mean về 0 (shift)
    melody_centered = melody_smooth - np.mean(melody_smooth)
    # Scale biên độ về [-1, 1] (scale)
    max_abs = np.max(np.abs(melody_centered))
    if max_abs > 1e-8:
        melody_shape = melody_centered / max_abs
    else:
        melody_shape = melody_centered
    
    return melody_shape

def extract_contour_features(melody_contour):
    """Extract advanced contour features for better shape matching"""
    if len(melody_contour) < 5:
        return {
            'slope_pattern': np.zeros(10),
            'peak_pattern': np.zeros(10)
        }
    
    # 1. Enhanced slope patterns
    slopes = np.gradient(melody_contour)
    slope_threshold = np.std(slopes) * 0.15  # Giảm threshold để nhạy hơn
    slope_pattern = np.where(slopes > slope_threshold, 1, 
                            np.where(slopes < -slope_threshold, -1, 0))
    
    # 2. Multi-scale peak detection
    min_distance = max(2, len(melody_contour) // 25)
    
    # Detect peaks with different prominence levels
    peaks_strong, _ = find_peaks(melody_contour, distance=min_distance, prominence=0.15)
    peaks_weak, _ = find_peaks(melody_contour, distance=min_distance//2, prominence=0.05)
    
    valleys_strong, _ = find_peaks(-melody_contour, distance=min_distance, prominence=0.15)
    valleys_weak, _ = find_peaks(-melody_contour, distance=min_distance//2, prominence=0.05)
    
    # Create peak pattern with different weights
    peak_pattern = np.zeros_like(melody_contour)
    peak_pattern[peaks_strong] = 1.0      # Strong peaks
    peak_pattern[peaks_weak] = 0.5        # Weak peaks
    peak_pattern[valleys_strong] = -1.0   # Strong valleys  
    peak_pattern[valleys_weak] = -0.5     # Weak valleys
    
    return {
        'slope_pattern': slope_pattern,
        'peak_pattern': peak_pattern
    }

def create_melody_signature(contour_features, target_length=20):
    """Create fixed-length signature preserving important features"""
    signature = []
    
    for feature_name, feature_data in contour_features.items():
        if len(feature_data) == 0:
            signature.extend([0.0] * (target_length // 2))
        else:
            # Use interpolation to preserve shape
            target_len = target_length // 2
            resampled = np.interp(
                np.linspace(0, len(feature_data)-1, target_len),
                np.arange(len(feature_data)), 
                feature_data
            )
            signature.extend(resampled)
    
    return np.array(signature)

def extract_features(file_path, mode="melody_silhouette"):
    """Extract melody silhouette features - optimized version"""
    try:
        y, sr = load_audio(file_path)
        if len(y) == 0:
            raise ValueError("Empty audio data")
            
        if mode == "melody_silhouette":
            print(f"Extracting melody from {file_path.split('/')[-1]}...")
            
            melody_seq, success = extract_melody_line(y, sr)
            if not success:
                raise ValueError("Failed to extract melody")
            
            melody_contour = normalize_melody_contour(melody_seq)
            if len(melody_contour) == 0:
                raise ValueError("Invalid melody contour")
            
            contour_features = extract_contour_features(melody_contour)
            melody_signature = create_melody_signature(contour_features, target_length=20)
            
            # Reshape như code gốc
            features_2d = melody_signature.reshape(2, -1)
            
            print(f"  ✓ Melody signature: {features_2d.shape}")
            return features_2d
        else:
            # Fallback MFCC
            mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=8, hop_length=512)
            chroma = librosa.feature.chroma_stft(y=y, sr=sr, hop_length=512)
            return np.vstack((mfcc, chroma))
            
    except Exception as e:
        print(f"Error extracting features from {file_path}: {e}")
        return np.array([])