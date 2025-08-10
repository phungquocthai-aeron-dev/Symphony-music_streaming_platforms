import os
import numpy as np
import pickle
from utils import extract_features

AUDIO_DIR = "data/audio"    # thư mục chứa file âm thanh
FEATURES_DIR = "data/features"  # thư mục lưu features

# Tạo thư mục nếu chưa tồn tại
os.makedirs(FEATURES_DIR, exist_ok=True)

features_dict = {}
labels_list = []

print("🎵 Starting feature extraction...")
print("=" * 50)

for filename in os.listdir(AUDIO_DIR):
    if filename.endswith(".mp3") or filename.endswith(".wav"):
        file_path = os.path.join(AUDIO_DIR, filename)
        print(f"Processing {file_path}...")
        
        try:
            features = extract_features(file_path)
            
            if features.size == 0:
                print(f"  ❌ Failed to extract features from {filename}")
                continue
            
            # Lưu features của từng file riêng biệt với tên đúng
            song_name = os.path.splitext(filename)[0]  # Tên file không có extension
            features_dict[song_name] = features
            
            # Tạo label từ tên file
            if "_" in filename:
                label = filename.split("_")[0]
            else:
                label = song_name
            labels_list.append(label)
            
            print(f"  ✅ {song_name}: {features.shape}")
            
        except Exception as e:
            print(f"  ❌ Error processing {filename}: {e}")
            continue

# Lưu features dictionary và labels
features_file = os.path.join(FEATURES_DIR, "features.pkl")
labels_file = os.path.join(FEATURES_DIR, "labels.npy")

print("\n💾 Saving features...")

# QUAN TRỌNG: Lưu dictionary bằng pickle
try:
    with open(features_file, "wb") as f:
        pickle.dump(features_dict, f)
    print(f"✅ Saved features dictionary to {features_file}")
    print(f"📊 Dictionary contains {len(features_dict)} songs:")
    
    # Show sample
    for i, (name, data) in enumerate(list(features_dict.items())[:3]):
        print(f"  {i+1}. {name}: {data.shape}")
    
except Exception as e:
    print(f"❌ Error saving features.pkl: {e}")

# Lưu labels
try:
    np.save(labels_file, np.array(labels_list))
    print(f"✅ Saved {len(labels_list)} labels to {labels_file}")
except Exception as e:
    print(f"❌ Error saving labels: {e}")

print("\n🎯 Feature extraction completed!")
print(f"📈 Successfully processed: {len(features_dict)}/{len([f for f in os.listdir(AUDIO_DIR) if f.endswith(('.mp3', '.wav'))])} files")

# Verify the saved file
try:
    with open(features_file, "rb") as f:
        verification = pickle.load(f)
    print(f"🔍 Verification: {len(verification)} items saved correctly")
except:
    print("⚠️  Warning: Could not verify saved file")