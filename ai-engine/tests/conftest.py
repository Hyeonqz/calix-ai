"""
Pytest configuration and shared fixtures.

This module provides test fixtures that can be used across all test files.
"""

import pytest
from fastapi.testclient import TestClient

from app.main import app


@pytest.fixture
def client() -> TestClient:
    """
    Provide a test client for making API requests.

    This fixture creates a TestClient instance that can be used
    to test API endpoints without actually running the server.

    Returns:
        TestClient: FastAPI test client

    Example:
        >>> def test_endpoint(client):
        ...     response = client.get("/api/v1/health")
        ...     assert response.status_code == 200
    """
    return TestClient(app)


@pytest.fixture
def valid_stock_ticker() -> str:
    """
    Provide a valid stock ticker for testing.

    Returns:
        str: Valid stock ticker symbol
    """
    return "AAPL"


@pytest.fixture
def invalid_stock_ticker() -> str:
    """
    Provide an invalid stock ticker for testing error cases.

    Returns:
        str: Invalid stock ticker symbol
    """
    return "INVALID_TICKER_123"
