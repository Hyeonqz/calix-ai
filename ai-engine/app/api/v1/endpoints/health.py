"""
Health check endpoints.

These endpoints are used to monitor the service health and readiness.
"""

from fastapi import APIRouter, Depends

from app.api.dependencies import get_app_settings
from app.config.settings import Settings
from app.schemas.base import BaseResponse

router = APIRouter(prefix="/health", tags=["health"])


@router.get("", response_model=BaseResponse)
async def health_check(settings: Settings = Depends(get_app_settings)) -> BaseResponse:
    """
    Basic health check endpoint.

    Returns service status and basic information.
    Use this for simple liveness checks.

    Returns:
        BaseResponse: Health status
    """
    return BaseResponse(
        success=True,
        message=f"{settings.app_name} v{settings.app_version} is healthy"
    )


@router.get("/ready", response_model=BaseResponse)
async def readiness_check(settings: Settings = Depends(get_app_settings)) -> BaseResponse:
    """
    Readiness check endpoint.

    Checks if the service is ready to accept requests.
    Can be extended to check external dependencies (APIs, etc.) if needed.

    Returns:
        BaseResponse: Readiness status
    """
    # In the future, add checks for:
    # - External API connectivity (OpenAI, etc.)
    # - Required environment variables
    # - ML model loading status

    return BaseResponse(
        success=True,
        message=f"{settings.app_name} is ready to serve requests"
    )
