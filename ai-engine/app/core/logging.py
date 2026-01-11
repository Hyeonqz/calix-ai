"""
Logging configuration for AI Engine.

This module provides structured logging setup and logger factory functions.
"""

import logging
import sys

from app.config.settings import get_settings


def setup_logging() -> None:
    """
    Setup application-wide logging configuration.

    This function should be called once at application startup.
    It configures the root logger and sets appropriate log levels
    for external libraries.
    """
    settings = get_settings()

    # Configure root logger
    logging.basicConfig(
        level=getattr(logging, settings.log_level.upper()),
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        handlers=[logging.StreamHandler(sys.stdout)],
    )

    # Set external library log levels to reduce noise
    logging.getLogger("uvicorn").setLevel(logging.INFO)
    logging.getLogger("fastapi").setLevel(logging.INFO)
    logging.getLogger("httpx").setLevel(logging.WARNING)
    logging.getLogger("httpcore").setLevel(logging.WARNING)


def get_logger(name: str) -> logging.Logger:
    """
    Get a logger instance for a specific module.

    Args:
        name: Logger name (typically __name__ from calling module)

    Returns:
        logging.Logger: Configured logger instance

    Example:
        >>> from app.core.logging import get_logger
        >>> logger = get_logger(__name__)
        >>> logger.info("Processing request")
    """
    return logging.getLogger(name)
