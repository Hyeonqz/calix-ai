"""
Stock-related API endpoints.

This module handles all stock-related HTTP requests from Spring Boot server.
"""

import logging

from fastapi import APIRouter, Depends

from app.api.dependencies import get_request_logger
from app.schemas.base import DataResponse
from app.schemas.stock import StockPriceRequest, StockPriceSchema
from app.services.stock_service import StockService

router = APIRouter(prefix="/stocks", tags=["stocks"])


@router.post("/price", response_model=DataResponse[StockPriceSchema])
async def get_stock_price(
    request: StockPriceRequest,
    logger: logging.Logger = Depends(get_request_logger)
) -> DataResponse[StockPriceSchema]:
    """
    Get current stock price for a given ticker.

    This endpoint is called by Spring Boot server to fetch real-time stock prices.
    The data is fetched from Yahoo Finance API.

    Args:
        request: Stock price request with ticker symbol
        logger: Logger dependency (injected automatically)

    Returns:
        DataResponse[StockPriceSchema]: Stock price data wrapped in standard response format

    Raises:
        ExternalAPIError: If Yahoo Finance API call fails (automatically handled by global exception handler)

    Example Request (from Spring Boot):
        POST /api/v1/stocks/price
        {
            "ticker": "AAPL"
        }

    Example Response:
        {
            "success": true,
            "message": "Stock price retrieved successfully for AAPL",
            "data": {
                "ticker": "AAPL",
                "current_price": 182.52,
                "currency": "USD",
                "market_status": "open"
            }
        }
    """
    logger.info(f"Received stock price request for ticker: {request.ticker}")

    # Create service instance and fetch data
    service = StockService()
    stock_data = await service.get_current_price(request.ticker)

    # Return wrapped response
    return DataResponse[StockPriceSchema](
        data=stock_data,
        message=f"Stock price retrieved successfully for {request.ticker}"
    )
