"""
Base response schemas for API endpoints.

This module defines the standard response format for all API endpoints.
All responses use these base models to ensure consistency across the API.
"""

from typing import Any, Generic, TypeVar

from pydantic import BaseModel, Field

# Generic type variable for data payload
T = TypeVar("T")


class BaseResponse(BaseModel):
    """
    Base response model for all API responses.

    All API responses should include success status and message fields.
    """

    success: bool = Field(default=True, description="Request success status")
    message: str = Field(default="Success", description="Response message")


class DataResponse(BaseResponse, Generic[T]):
    """
    Generic response model with typed data payload.

    Use this for successful responses that return data.
    The data field is typed using Generic[T] for type safety.

    Example:
        >>> from app.schemas.stock import StockPriceSchema
        >>> response = DataResponse[StockPriceSchema](
        ...     data=stock_data,
        ...     message="Stock price retrieved successfully"
        ... )
    """

    data: T = Field(..., description="Response data payload")


class ErrorResponse(BaseResponse):
    """
    Error response model.

    Use this for error responses with optional additional details.
    """

    success: bool = Field(default=False, description="Request success status (always False for errors)")
    message: str = Field(..., description="Error message")
    details: dict[str, Any] | None = Field(default=None, description="Additional error details")
