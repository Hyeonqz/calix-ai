import 'package:equatable/equatable.dart';

/// 앱 전반에서 사용되는 실패(에러) 클래스입니다.
abstract class Failure extends Equatable {
  final String message;

  const Failure(this.message);

  @override
  List<Object?> get props => [message];
}

/// 서버 에러
class ServerFailure extends Failure {
  const ServerFailure([super.message = 'Server Error']);
}

/// 네트워크 에러
class NetworkFailure extends Failure {
  const NetworkFailure([super.message = 'Network Error']);
}

/// 캐시 에러
class CacheFailure extends Failure {
  const CacheFailure([super.message = 'Cache Error']);
}

/// 인증 에러
class AuthFailure extends Failure {
  const AuthFailure([super.message = 'Authentication Error']);
}

/// 유효성 검사 에러
class ValidationFailure extends Failure {
  const ValidationFailure([super.message = 'Validation Error']);
}

/// 알 수 없는 에러
class UnknownFailure extends Failure {
  const UnknownFailure([super.message = 'Unknown Error']);
}
