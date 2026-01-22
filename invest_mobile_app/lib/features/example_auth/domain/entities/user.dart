import 'package:equatable/equatable.dart';

/// 사용자 엔티티 (도메인 모델)
///
/// 순수한 비즈니스 로직 모델로, 외부 의존성이 없습니다.
class User extends Equatable {
  final String id;
  final String email;
  final String name;

  const User({
    required this.id,
    required this.email,
    required this.name,
  });

  @override
  List<Object?> get props => [id, email, name];
}
