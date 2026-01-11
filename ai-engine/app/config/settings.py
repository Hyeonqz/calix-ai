"""
Application configuration management using Pydantic Settings.

This module provides type-safe configuration management with environment variable support.
No database connection settings - this is a stateless API service.
"""

from functools import lru_cache
from typing import Literal

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """
    Application settings with environment variable support.

    All settings can be overridden via environment variables or .env file.
    Settings are loaded once at startup and cached for performance.
    """

    # Application Settings
    app_name: str = "Calix AI Engine"
    app_version: str = "1.0.0"
    environment: Literal["development", "staging", "production"] = "development"
    debug: bool = False

    # API Settings
    api_v1_prefix: str = "/api/v1"
    allowed_origins: list[str] = ["http://localhost:8080", "http://localhost:3000"]

    # External API Keys (NO DATABASE CREDENTIALS!)
    openai_api_key: str | None = None
    langchain_api_key: str | None = None

    # Logging Configuration
    log_level: str = "INFO"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore"
    )


@lru_cache
def get_settings() -> Settings:
    """
    Get application settings singleton.

    Uses @lru_cache to ensure settings are loaded only once.
    This improves performance and ensures consistency across the application.

    Returns:
        Settings: Application settings instance
    """
    return Settings()
