from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import shutil
import os
from predict import predict
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*'],
)

TEMP_DIR = os.path.join(os.path.dirname(__file__), 'uploads')
os.makedirs(TEMP_DIR, exist_ok=True)

@app.post('/search-humming')
async def search_humming(file: UploadFile = File(...), top_k: int = 5):
    if not file.filename.lower().endswith(('.wav', '.mp3')):
        raise HTTPException(status_code=400, detail='Invalid file type. Only WAV or MP3 allowed.')
    tmp_path = os.path.join(TEMP_DIR, f'input{os.path.splitext(file.filename)[1]}')
    try:
        with open(tmp_path, 'wb') as buffer:
            shutil.copyfileobj(file.file, buffer)
        results = predict(tmp_path, top_k=top_k)
        return {'results': results}
    except Exception as e:
        logger.error(f"Search failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        try:
            os.remove(tmp_path)
        except:
            pass