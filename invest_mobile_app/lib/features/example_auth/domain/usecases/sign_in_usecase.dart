import 'package:dartz/dartz.dart';
import '../../../../core/error/failures.dart';
import '../entities/user.dart';
import '../repositories/auth_repository.dart';

/// 로그인 UseCase
///
/// 단일 책임 원칙에 따라 하나의 비즈니스 로직만 처리합니다.
class SignInUseCase {
  final AuthRepository repository;

  SignInUseCase(this.repository);

  /// 로그인 실행
  Future<Either<Failure, User>> execute({
    required String email,
    required String password,
  }) async {
    // 입력값 검증
    if (email.isEmpty || !_isValidEmail(email)) {
      return const Left(ValidationFailure('유효하지 않은 이메일입니다.'));
    }

    if (password.isEmpty || password.length < 6) {
      return const Left(ValidationFailure('비밀번호는 6자 이상이어야 합니다.'));
    }

    // Repository를 통한 로그인 실행
    return await repository.signInWithEmail(
      email: email,
      password: password,
    );
  }

  bool _isValidEmail(String email) {
    return RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(email);
  }
}
