"""
Stock data processing service.

This module contains business logic for fetching and processing stock data.
"""

import yfinance as yf

from app.core.errors import ExternalAPIError
from app.core.logging import get_logger
from app.schemas.stock import StockPriceSchema

logger = get_logger(__name__)


class StockService:
    """
    Service for stock data operations.

    This service encapsulates all business logic related to stock data processing.
    It serves as a reference implementation for the service layer pattern.
    """

    async def get_current_price(self, ticker: str) -> StockPriceSchema:
        """
        Fetch current stock price from Yahoo Finance.

        This is an async method to maintain consistency with the async-first architecture,
        even though yfinance is a synchronous library. For truly async external API calls,
        use httpx or aiohttp.

        Args:
            ticker: Stock ticker symbol (e.g., "AAPL", "GOOGL")

        Returns:
            StockPriceSchema: Current stock price and related information

        Raises:
            ExternalAPIError: If the API call fails or ticker is invalid
        """
        logger.info(f"Fetching stock price for ticker: {ticker}")

        try:
            # Fetch stock data using yfinance
            stock = yf.Ticker(ticker)
            price_info = stock.fast_info

            # Extract price data
            current_price = price_info.last_price
            currency = price_info.currency

            # Create response schema
            result = StockPriceSchema(
                ticker=ticker.upper(),
                current_price=round(current_price, 2),
                currency=currency,
                market_status="open"  # Simplified - can be enhanced with market hours check
            )

            logger.info(f"Successfully fetched price for {ticker}: {current_price} {currency}")
            return result

        except AttributeError as e:
            # This occurs when ticker is invalid or data is not available
            logger.error(f"Invalid ticker or data not available for {ticker}: {str(e)}")
            raise ExternalAPIError(
                message=f"Unable to fetch stock data for ticker: {ticker}",
                details={"ticker": ticker, "error": "Invalid ticker or data not available"}
            )
        except Exception as e:
            # Catch all other exceptions
            logger.error(f"Failed to fetch stock price for {ticker}: {str(e)}")
            raise ExternalAPIError(
                message=f"External API error while fetching stock data for {ticker}",
                details={"ticker": ticker, "error": str(e)}
            )
