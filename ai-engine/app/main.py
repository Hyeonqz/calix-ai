# ai-engine/app/main.py
# 실행 가이드
# $ uvicorn main:app --reload
# $ uvicorn app.main:app --reload --port 8000
from fastapi import FastAPI
import yfinance as yf

app = FastAPI()

@app.get("/")
def read_root():
    return {"status": "AI Engine is running"}


@app.get("/stock/{ticker}")
def get_stock_price(ticker: str):
    # Java 개발자에게 익숙한 데이터 처리
    stock = yf.Ticker(ticker)
    price_info = stock.fast_info

    return {
        "ticker": ticker,
        "current_price": price_info.last_price,
        "currency": price_info.currency
    }