import os
import pickle
import numpy as np
from loguru import logger

FEATURES_DIR = "data"
OUTPUT_FILE = "models/embeddings.pkl"

def build_embeddings():
    embeddings = {}
    
    if not os.path.exists(FEATURES_DIR):
        logger.error(f"Features directory not found: {FEATURES_DIR}")
        return
    
    features_file = os.path.join(FEATURES_DIR, "features.pkl")
    
    if os.path.exists(features_file):
        try:
            with open(features_file, "rb") as f:
                features_dict = pickle.load(f)
            
            embeddings.update(features_dict)
            logger.info(f"Loaded {len(features_dict)} features from features.pkl")
            
            for i, (name, data) in enumerate(list(features_dict.items())[:3]):
                logger.info(f"  {name}: {data.shape}")
                
        except Exception as e:
            logger.error(f"Failed to load features.pkl: {e}")
    else:
        logger.warning(f"features.pkl not found at {features_file}")
    
    for file in os.listdir(FEATURES_DIR):
        if file.lower().endswith(".npy") and file != "labels.npy":
            feature_path = os.path.join(FEATURES_DIR, file)
            try:
                data = np.load(feature_path)
                key = os.path.splitext(file)[0]
                
                if key not in embeddings:
                    embeddings[key] = data
                    logger.info(f"Loaded additional features from {file}: {data.shape}")
                    
            except Exception as e:
                logger.error(f"Failed to load {file}: {e}")
    
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    
    with open(OUTPUT_FILE, "wb") as f:
        pickle.dump(embeddings, f)
    
    logger.info(f"Các vector nhúng cuối cùng đã được lưu vào thư mục {OUTPUT_FILE}")
    logger.info(f"Tổng số bài hát trong database: {len(embeddings)}")
    
    if embeddings:
        sample_shapes = [data.shape for data in list(embeddings.values())[:5]]
        logger.info(f"Sample shapes: {sample_shapes}")
    
    return embeddings

if __name__ == "__main__":
    build_embeddings()
