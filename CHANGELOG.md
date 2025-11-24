# Changelog

All notable changes to SlideIT project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.4] - 2025-11-22

### Fixed
- Navigation 구조 전면 개선
  - 불필요한 투명 NavHost 제거
  - Settings 화면 navigation 수정
  - CardEditor 저장 후 명함 공유 화면으로 올바르게 이동
  - navigateUp() 대신 popBackStack() 사용으로 안정성 향상
- isVisible 파라미터 추가로 NavHost 렌더링 최적화

### Removed
- 불필요한 "empty" route 제거
- 투명 NavHost 중복 렌더링 제거
- startDestination 파라미터 제거 (항상 CardEditor.route 사용)

## [0.0.3] - 2025-11-22

### Fixed
- 명함 에디터에서 저장 후 명함 공유 화면으로 올바르게 이동하도록 수정
- Canvas 에디터에서 뒤로가기 버튼 작동하도록 수정
  - MainActivity의 showBottomNav 리스트에 "canvas_editor" 추가

### Added
- 설정 화면에 "받은 명함 전체 삭제" 기능 추가
  - 데이터 관리 섹션에 위치
  - 삭제 전 확인 다이얼로그 표시

## [0.0.2] - 2025-11-22

### Fixed
- 내 명함 수정 시 반영되지 않던 문제 해결
  - ProfileScreen에서 내 명함 수정 시 기존 명함 데이터를 에디터로 전달하도록 수정
  - MainActivity에서 cardToEdit 설정 수정
- 내 명함이 명함 공유 화면에 표시되도록 수정

## [0.0.1] - 2025-11-22

### Added
- 초기 프로젝트 구조 설정
- Room 데이터베이스를 사용한 온디바이스 명함 저장
- 명함 공유 화면 (CardShareScreen)
  - 3D 홀로그래픽 효과가 있는 명함 렌더링
  - 드래그 제스처를 통한 입체 3D 효과
  - 내 명함 공유 및 저장 기능
- 명함 보관함 화면 (CardStorageScreen)
  - 공유받은 명함 목록 표시 (내 명함 제외)
  - 명함 검색 기능
  - 즐겨찾기 기능
  - 명함 확대보기 다이얼로그
- 프로필 화면 (ProfileScreen)
  - 내 명함 생성 및 수정
  - Simple/Canvas 두 가지 에디터 타입 지원
- Canvas 에디터 (CardCanvasEditorScreen)
  - 자유로운 텍스트, 이미지, 도형 요소 배치
  - 드래그로 요소 이동
  - 요소 선택 및 삭제
  - 요소 레이어 순서 조정
  - Canvas 데이터 JSON 직렬화/역직렬화
- 통합 명함 렌더링 시스템 (CardRenderer)
  - Simple 명함 렌더러
  - Canvas 명함 렌더러
  - 3D 회전 효과 및 홀로그래픽 glare 효과
- Bitmap 변환 유틸리티 (BitmapConverter)
  - Composable을 Bitmap으로 변환
  - 갤러리 저장 기능
  - 다른 앱으로 공유 기능
  - FileProvider 설정
- 설정 화면 (SettingsScreen)
  - CSV 내보내기/가져오기
  - 테마 설정
  - 알림 설정
- Navigation 구조
  - Bottom Navigation Bar
  - 화면 간 이동 처리

### Technical Details
- Kotlin + Jetpack Compose
- MVVM Architecture
- Room Database
- Material3 Design
- Coroutines & Flow
- FileProvider for secure file sharing
- Gson for JSON serialization

### Database Schema
- BusinessCard entity with fields:
  - Basic info: name, position, department, company, email, phone, address
  - Visual: backgroundColor, textColor, accentColor, imageUrl
  - Metadata: isMyCard, isFavorite, category, tags, memo
  - Editor: editorType (SIMPLE/CANVAS), canvasData (JSON)
  - Timestamps: createdAt, lastModifiedAt
