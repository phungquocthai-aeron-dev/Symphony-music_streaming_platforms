import os
import numpy as np
import pickle
from utils import extract_features

AUDIO_DIRS = ["../uploads/music/normal", "../uploads/music/vip"]
FEATURES_DIR = "data"

os.makedirs(FEATURES_DIR, exist_ok=True)

features_dict = {}
labels_list = []

print("Starting feature extraction...")
print("=" * 50)

for AUDIO_DIR in AUDIO_DIRS:
    print(f"\nProcessing directory: {AUDIO_DIR}")
    print("-" * 50)
    
    for filename in os.listdir(AUDIO_DIR):
        if filename.endswith(".mp3") or filename.endswith(".wav"):
            file_path = os.path.join(AUDIO_DIR, filename)
            print(f"Processing {file_path}...")
            
            try:
                features = extract_features(file_path)
                
                if features.size == 0:
                    print(f"  Failed to extract features from {filename}")
                    continue
                
                song_name = os.path.splitext(filename)[0]
                features_dict[song_name] = features
                
                if "_" in filename:
                    label = filename.split("_")[0]
                else:
                    label = song_name
                labels_list.append(label)
                
                print(f"  {song_name}: {features.shape}")
                
            except Exception as e:
                print(f"  Error processing {filename}: {e}")
                continue

features_file = os.path.join(FEATURES_DIR, "features.pkl")
labels_file = os.path.join(FEATURES_DIR, "labels.npy")

print("\nLưu các đặc trưng...")

try:
    with open(features_file, "wb") as f:
        pickle.dump(features_dict, f)
    print(f"Saved features dictionary to {features_file}")
    print(f"Thư mục bao gồm {len(features_dict)} bài hát:")
    
    for i, (name, data) in enumerate(list(features_dict.items())[:3]):
        print(f"  {i+1}. {name}: {data.shape}")
    
except Exception as e:
    print(f"Error saving features.pkl: {e}")

try:
    np.save(labels_file, np.array(labels_list))
    print(f"Saved {len(labels_list)} labels to {labels_file}")
except Exception as e:
    print(f"Lỗi lưu nhãn: {e}")

print("\nTrích xuất đặc trưng thành công!")
total_files = sum(len([f for f in os.listdir(dir) if f.endswith(('.mp3', '.wav'))]) for dir in AUDIO_DIRS)
print(f"Xử lý thành công: {len(features_dict)}/{total_files} files")

try:
    with open(features_file, "rb") as f:
        verification = pickle.load(f)
    print(f"Xác minh: {len(verification)} mục được lưu chính xác")
except:
    print("Warning: Could not verify saved file")
