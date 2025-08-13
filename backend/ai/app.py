from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import shutil
import os, pickle
from predict import predict
import logging
from extract_features import extract_features

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

MODEL_PATH = "models/embeddings.pkl"
TEMP_DIR = os.path.join(os.path.dirname(__file__), 'uploads')
os.makedirs(TEMP_DIR, exist_ok=True)

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*'],
)

@app.post('/search-humming')
async def search_humming(file: UploadFile = File(...), top_k: int = 5):
    if not file.filename.lower().endswith(('.mp3')):
        raise HTTPException(status_code=400, detail='Invalid file type. Only WAV or MP3 allowed.')

    tmp_path = os.path.join(TEMP_DIR, f'input{os.path.splitext(file.filename)[1]}')

    try:
        with open(tmp_path, 'wb') as buffer:
            shutil.copyfileobj(file.file, buffer)

        results = predict(tmp_path)  # Hàm predict mới trả về đầy đủ kết quả có confidence

        results = sorted(results, key=lambda x: x['confidence'], reverse=True)

        logger.info("Kết quả so sánh:")
        for r in results:
            logger.info(f"Bài: {r['song']}, độ giống: {r['confidence']:.1f}%")

        if not results:
            return {"songs": []}

        high_conf = [r for r in results if r['confidence'] >= 90.0]

        if len(high_conf) >= top_k:
            selected = high_conf
            logger.info("A")
        else:
            selected = results[:top_k]
            logger.info(selected)

        songs = [r['song'] for r in selected]

        return {"songs": songs}

    except Exception as e:
        logger.error(f"Search failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))

    finally:
        try:
            os.remove(tmp_path)
        except:
            pass

@app.post('/search-humming-detail')
async def search_humming_detail(file: UploadFile = File(...), top_k: int = 5):
    if not file.filename.lower().endswith(('.wav', '.mp3')):
        raise HTTPException(status_code=400, detail='Invalid file type. Only WAV or MP3 allowed.')
    tmp_path = os.path.join(TEMP_DIR, f'input{os.path.splitext(file.filename)[1]}')
    try:
        with open(tmp_path, 'wb') as buffer:
            shutil.copyfileobj(file.file, buffer)
        results = predict(tmp_path)  # List of dicts: {"song": str, "confidence": float}

        if not results:
            return {"songs": []}

        # Lọc theo logic: nếu >= 5 bài từ 99.99% trở lên thì lấy hết nhóm đó
        high_conf = [r for r in results if r['confidence'] >= 99.99]
        if len(high_conf) > top_k:
            selected = high_conf
        else:
            selected = results[:top_k]

        return {"songs": selected}

    except Exception as e:
        logger.error(f"Search failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        try:
            os.remove(tmp_path)
        except:
            pass


@app.post("/update-model")
async def update_model(song_path: str):
    try:
        # Tách tên file
        song_name = os.path.basename(song_path)

        # Xây đường dẫn tuyệt đối tới file thật
        # __file__ = file hiện tại, .. = lên 1 cấp, uploads = thư mục chứa nhạc
        base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'uploads'))
        file_path = os.path.join(base_dir, song_path.lstrip("/"))  
        # "/music/normal/abc.mp3" -> "../uploads/music/normal/abc.mp3"

        if not os.path.exists(file_path):
            raise HTTPException(status_code=404, detail=f"File not found: {file_path}")

        # Trích xuất đặc trưng
        embedding = extract_features(file_path)

        # Nạp hoặc khởi tạo model
        if os.path.exists(MODEL_PATH):
            with open(MODEL_PATH, "rb") as f:
                data = pickle.load(f)
        else:
            data = {}

        # Cập nhật
        data[song_name] = embedding

        with open(MODEL_PATH, "wb") as f:
            pickle.dump(data, f)

        return {
            "message": f"Song '{song_name}' added to model.",
            "total_songs": len(data)
        }

    except Exception as e:
        logger.error(f"Update model failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))
    

@app.post("/remove-song")
async def remove_song(song_path: str):
    try:
        # Tách tên bài hát
        song_name = os.path.basename(song_path)

        if not os.path.exists(MODEL_PATH):
            raise HTTPException(status_code=404, detail="Model file not found")

        with open(MODEL_PATH, "rb") as f:
            data = pickle.load(f)

        if song_name not in data:
            raise HTTPException(status_code=404, detail="Song not found in model")

        del data[song_name]

        with open(MODEL_PATH, "wb") as f:
            pickle.dump(data, f)

        return {"message": f"Song '{song_name}' removed from model.", "total_songs": len(data)}

    except Exception as e:
        logger.error(f"Remove song failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))
