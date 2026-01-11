"""
Tests for health check endpoints.
"""

from fastapi.testclient import TestClient


def test_health_check(client: TestClient):
    """
    Test basic health check endpoint.

    Args:
        client: FastAPI test client fixture
    """
    response = client.get("/api/v1/health")

    assert response.status_code == 200
    data = response.json()

    assert "success" in data
    assert data["success"] is True
    assert "message" in data
    assert "healthy" in data["message"].lower()


def test_readiness_check(client: TestClient):
    """
    Test readiness check endpoint.

    Args:
        client: FastAPI test client fixture
    """
    response = client.get("/api/v1/health/ready")

    assert response.status_code == 200
    data = response.json()

    assert "success" in data
    assert data["success"] is True
    assert "message" in data
    assert "ready" in data["message"].lower()


def test_root_endpoint(client: TestClient):
    """
    Test root endpoint (backward compatibility).

    Args:
        client: FastAPI test client fixture
    """
    response = client.get("/")

    assert response.status_code == 200
    data = response.json()

    assert "status" in data
    assert data["status"] == "running"
    assert "service" in data
    assert "version" in data
