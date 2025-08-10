import os
import pickle
import numpy as np
from loguru import logger

FEATURES_DIR = "data/features"
OUTPUT_FILE = "models/embeddings.pkl"

def build_embeddings():
    embeddings = {}
    
    if not os.path.exists(FEATURES_DIR):
        logger.error(f"Features directory not found: {FEATURES_DIR}")
        return
    
    # CHÍNH: Load features từ file pickle (chứa dictionary)
    features_file = os.path.join(FEATURES_DIR, "features.pkl")
    
    if os.path.exists(features_file):
        try:
            with open(features_file, "rb") as f:
                features_dict = pickle.load(f)
            
            # Copy toàn bộ dictionary
            embeddings.update(features_dict)
            logger.info(f"✅ Loaded {len(features_dict)} features from features.pkl")
            
            # Debug: Show some examples
            for i, (name, data) in enumerate(list(features_dict.items())[:3]):
                logger.info(f"  📝 {name}: {data.shape}")
                
        except Exception as e:
            logger.error(f"❌ Failed to load features.pkl: {e}")
    else:
        logger.warning(f"⚠️  features.pkl not found at {features_file}")
    
    # PHỤ: Kiểm tra các file .npy khác (backup)
    for file in os.listdir(FEATURES_DIR):
        if file.lower().endswith(".npy") and file != "labels.npy":
            feature_path = os.path.join(FEATURES_DIR, file)
            try:
                data = np.load(feature_path)
                key = os.path.splitext(file)[0]
                
                # Chỉ thêm nếu chưa có từ pickle
                if key not in embeddings:
                    embeddings[key] = data
                    logger.info(f"📎 Loaded additional features from {file}: {data.shape}")
                    
            except Exception as e:
                logger.error(f"❌ Failed to load {file}: {e}")
    
    # Lưu embeddings
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    
    with open(OUTPUT_FILE, "wb") as f:
        pickle.dump(embeddings, f)
    
    logger.info(f"🎯 Final embeddings saved to {OUTPUT_FILE}")
    logger.info(f"📊 Total songs in database: {len(embeddings)}")
    
    # Show final summary
    if embeddings:
        sample_shapes = [data.shape for data in list(embeddings.values())[:5]]
        logger.info(f"📏 Sample shapes: {sample_shapes}")
    
    return embeddings

if __name__ == "__main__":
    build_embeddings()