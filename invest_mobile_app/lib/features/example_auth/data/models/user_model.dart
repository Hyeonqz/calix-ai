import '../../domain/entities/user.dart';

/// User 데이터 모델 (DTO)
///
/// API 응답이나 로컬 DB와 매핑되는 모델입니다.
/// Domain Entity로 변환하는 역할을 합니다.
class UserModel extends User {
  const UserModel({
    required super.id,
    required super.email,
    required super.name,
  });

  /// JSON에서 UserModel로 변환
  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] as String,
      email: json['email'] as String,
      name: json['name'] as String,
    );
  }

  /// UserModel을 JSON으로 변환
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'name': name,
    };
  }

  /// Domain Entity로 변환
  User toEntity() {
    return User(
      id: id,
      email: email,
      name: name,
    );
  }

  /// Domain Entity에서 변환
  factory UserModel.fromEntity(User user) {
    return UserModel(
      id: user.id,
      email: user.email,
      name: user.name,
    );
  }
}
