"""
Tests for stock endpoints.
"""

import pytest
from fastapi.testclient import TestClient


def test_get_stock_price_success(client: TestClient, valid_stock_ticker: str):
    """
    Test successful stock price retrieval.

    Args:
        client: FastAPI test client fixture
        valid_stock_ticker: Valid stock ticker fixture
    """
    response = client.post(
        "/api/v1/stocks/price",
        json={"ticker": valid_stock_ticker}
    )

    assert response.status_code == 200
    data = response.json()

    # Check response structure
    assert "success" in data
    assert data["success"] is True
    assert "message" in data
    assert "data" in data

    # Check data payload
    stock_data = data["data"]
    assert "ticker" in stock_data
    assert stock_data["ticker"] == valid_stock_ticker.upper()
    assert "current_price" in stock_data
    assert isinstance(stock_data["current_price"], (int, float))
    assert stock_data["current_price"] > 0
    assert "currency" in stock_data
    assert "market_status" in stock_data


def test_get_stock_price_invalid_ticker(client: TestClient, invalid_stock_ticker: str):
    """
    Test stock price retrieval with invalid ticker.

    This should return an error response.

    Args:
        client: FastAPI test client fixture
        invalid_stock_ticker: Invalid stock ticker fixture
    """
    response = client.post(
        "/api/v1/stocks/price",
        json={"ticker": invalid_stock_ticker}
    )

    assert response.status_code == 400
    data = response.json()

    # Check error response structure
    assert "success" in data
    assert data["success"] is False
    assert "message" in data
    assert "details" in data or "details" not in data  # details is optional


def test_get_stock_price_missing_ticker(client: TestClient):
    """
    Test stock price retrieval without ticker field.

    This should return a validation error.

    Args:
        client: FastAPI test client fixture
    """
    response = client.post(
        "/api/v1/stocks/price",
        json={}
    )

    assert response.status_code == 422  # Pydantic validation error


def test_get_stock_price_empty_ticker(client: TestClient):
    """
    Test stock price retrieval with empty ticker.

    This should return a validation error due to min_length constraint.

    Args:
        client: FastAPI test client fixture
    """
    response = client.post(
        "/api/v1/stocks/price",
        json={"ticker": ""}
    )

    assert response.status_code == 422  # Pydantic validation error
