"""
AI Engine Main Application.

FastAPI application entry point with middleware, routers, and exception handlers.

Running the application:
    $ uvicorn app.main:app --reload --port 8000

API Documentation:
    - Swagger UI: http://localhost:8000/docs
    - ReDoc: http://localhost:8000/redoc
"""

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.api.v1.router import api_router
from app.config.settings import get_settings
from app.core.errors import AIEngineException
from app.core.logging import setup_logging
from app.schemas.base import ErrorResponse

# Setup logging
setup_logging()

# Get settings
settings = get_settings()

# Create FastAPI application
app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    debug=settings.debug,
    description="AI Engine for financial data analysis and prediction models"
)

# Add CORS middleware for Spring Boot server integration
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Global exception handler for custom exceptions
@app.exception_handler(AIEngineException)
async def ai_engine_exception_handler(request: Request, exc: AIEngineException) -> JSONResponse:
    """
    Handle all AIEngineException and its subclasses.

    Returns a standardized error response with 400 status code.
    """
    return JSONResponse(
        status_code=400,
        content=ErrorResponse(
            success=False,
            message=exc.message,
            details=exc.details
        ).model_dump()
    )


# Include API v1 router
app.include_router(api_router, prefix=settings.api_v1_prefix)


# Root endpoint (for backward compatibility)
@app.get("/")
async def root() -> dict[str, str]:
    """
    Root endpoint for basic status check.

    For detailed health checks, use /api/v1/health endpoints.
    """
    return {
        "status": "running",
        "service": settings.app_name,
        "version": settings.app_version
    }