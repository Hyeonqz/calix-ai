/// 앱 전반에서 사용되는 상수들을 정의합니다.
class AppConstants {
  AppConstants._();

  // API 관련
  static const String baseUrl = 'https://api.example.com';
  static const int connectTimeout = 30000;
  static const int receiveTimeout = 30000;

  // 로컬 저장소 키
  static const String accessTokenKey = 'access_token';
  static const String refreshTokenKey = 'refresh_token';
  static const String userIdKey = 'user_id';

  // 앱 정보
  static const String appName = 'Invest Mobile App';
  static const String appVersion = '1.0.0';
}
