"""
Shared dependencies for API endpoints.

This module provides common dependencies that can be injected into API endpoints.
"""

import logging

from app.config.settings import Settings, get_settings
from app.core.logging import get_logger


def get_app_settings() -> Settings:
    """
    Dependency for getting application settings.

    This can be injected into any endpoint that needs access to settings.

    Returns:
        Settings: Application settings instance

    Example:
        >>> from fastapi import Depends
        >>> from app.api.dependencies import get_app_settings
        >>>
        >>> @router.get("/config")
        >>> async def get_config(settings: Settings = Depends(get_app_settings)):
        ...     return {"app_name": settings.app_name}
    """
    return get_settings()


def get_request_logger() -> logging.Logger:
    """
    Dependency for getting a logger instance.

    Returns:
        logging.Logger: Logger instance for API requests

    Example:
        >>> from fastapi import Depends
        >>> from app.api.dependencies import get_request_logger
        >>>
        >>> @router.post("/process")
        >>> async def process(logger: logging.Logger = Depends(get_request_logger)):
        ...     logger.info("Processing request")
        ...     return {"status": "ok"}
    """
    return get_logger("api")
