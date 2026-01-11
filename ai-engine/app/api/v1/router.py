"""
API v1 router aggregation.

This module aggregates all v1 API endpoint routers into a single router.
When adding new endpoints, import and include the router here.
"""

from fastapi import APIRouter

from app.api.v1.endpoints import health, stocks

# Create main API v1 router
api_router = APIRouter()

# Include all endpoint routers
api_router.include_router(health.router)
api_router.include_router(stocks.router)

# Future endpoints can be added here:
# from app.api.v1.endpoints import predictions
# api_router.include_router(predictions.router)
