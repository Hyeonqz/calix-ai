"""
Stock-related Pydantic schemas.

This module defines request and response models for stock-related endpoints.
"""

from pydantic import BaseModel, Field


class StockPriceRequest(BaseModel):
    """
    Request model for getting stock price.

    This is sent from Spring Boot server to FastAPI.
    """

    ticker: str = Field(..., min_length=1, max_length=10, description="Stock ticker symbol (e.g., AAPL, GOOGL)")

    class Config:
        json_schema_extra = {
            "example": {
                "ticker": "AAPL"
            }
        }


class StockPriceSchema(BaseModel):
    """
    Response model for stock price data.

    This is returned to Spring Boot server.
    """

    ticker: str = Field(..., description="Stock ticker symbol")
    current_price: float = Field(..., description="Current stock price")
    currency: str = Field(..., description="Currency code (e.g., USD, KRW)")
    market_status: str = Field(..., description="Market status (e.g., open, closed)")

    class Config:
        json_schema_extra = {
            "example": {
                "ticker": "AAPL",
                "current_price": 182.52,
                "currency": "USD",
                "market_status": "open"
            }
        }
