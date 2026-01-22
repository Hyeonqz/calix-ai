import 'package:dartz/dartz.dart';
import '../../../../core/error/failures.dart';
import '../entities/user.dart';

/// 인증 Repository 인터페이스
///
/// Domain 레이어에서 정의하고, Data 레이어에서 구현합니다.
/// 비즈니스 로직은 이 인터페이스에만 의존합니다.
abstract class AuthRepository {
  /// 이메일과 비밀번호로 로그인
  Future<Either<Failure, User>> signInWithEmail({
    required String email,
    required String password,
  });

  /// 회원가입
  Future<Either<Failure, User>> signUp({
    required String email,
    required String password,
    required String name,
  });

  /// 로그아웃
  Future<Either<Failure, void>> signOut();

  /// 현재 로그인된 사용자 정보 가져오기
  Future<Either<Failure, User?>> getCurrentUser();
}
