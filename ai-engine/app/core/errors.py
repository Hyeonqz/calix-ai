"""
Custom exception classes for AI Engine.

This module defines a hierarchy of custom exceptions used throughout the application.
All exceptions inherit from AIEngineException base class.
"""

from typing import Any


class AIEngineException(Exception):
    """
    Base exception for AI Engine.

    All custom exceptions should inherit from this class.
    Provides a consistent interface for error handling.
    """

    def __init__(self, message: str, details: dict[str, Any] | None = None):
        """
        Initialize the exception.

        Args:
            message: Human-readable error message
            details: Additional error details (optional)
        """
        self.message = message
        self.details = details or {}
        super().__init__(self.message)


class ExternalAPIError(AIEngineException):
    """
    Exception raised when external API calls fail.

    Examples: Yahoo Finance API, OpenAI API, LangChain API failures
    """

    pass


class ModelInferenceError(AIEngineException):
    """
    Exception raised when ML model inference fails.

    Examples: Model loading failures, prediction errors
    """

    pass


class ValidationError(AIEngineException):
    """
    Exception raised when input validation fails.

    Note: This is different from Pydantic's ValidationError.
    Use this for business logic validation failures.
    """

    pass
