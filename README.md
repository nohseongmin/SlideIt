# SlideIT - 모바일 명함 편집 및 공유 플랫폼

## 📱 프로젝트 개요
명함을 모바일로 제작하여 서로 주고받고, 기존 명함을 저장하여 아카이빙하는 Android 애플리케이션

### 주요 기능
- 명함 편집 (간편/상세)
- 명함 공유 (슬라이드 제스처)
- 명함 보관함 (검색/정렬)
- OCR 명함 인식
- 다크 테마 지원

---

## 🚧 진행 중인 작업: 명함 에디터 대개편

### 목표
현재 텍스트 폼 기반 에디터를 **인스타그램 스토리 스타일 캔버스 에디터**로 확장

### 구현 방식
1. **간편 편집** (기존): 텍스트 필드로 빠르게 입력
2. **상세 편집** (신규): 이미지 편집기로 자유롭게 디자인
3. **통합 렌더링**: 두 방식 모두 이미지로 일관되게 표시

---

## 📋 구현 계획

### Phase 1: 데이터 모델 확장 (0.5일) ✅ 완료
**목표**: BusinessCard 모델에 Canvas 편집 데이터 추가

- [x] BusinessCard에 `editorType` 필드 추가
- [x] BusinessCard에 `canvasData` 필드 추가 (JSON)
- [x] BusinessCard에 `thumbnailPath` 필드 추가
- [x] CardElement sealed class 생성
  - [x] TextElement
  - [x] ImageElement
  - [x] ShapeElement
- [x] CanvasCardData 데이터 클래스 생성
- [x] TypeConverter 추가 (CardElement ↔ JSON)
- [x] Database version 3으로 업데이트

**파일 수정**:
- `app/src/main/java/com/example/slideit/data/model/BusinessCard.kt` ✅
- `app/src/main/java/com/example/slideit/data/model/CardElement.kt` ✅ (신규)
- `app/src/main/java/com/example/slideit/data/database/AppDatabase.kt` ✅

**완료 시각**: 2025-01-21
**빌드 상태**: ✅ BUILD SUCCESSFUL

---

### Phase 2: 에디터 선택 화면 (0.5일) ✅ 완료
**목표**: 명함 생성 시 편집 방식 선택 UI

- [x] EditorTypeSelectionDialog Composable 생성
- [x] 간편 편집 옵션 UI (그라데이션 카드)
- [x] 상세 편집 옵션 UI (그라데이션 카드)
- [x] MainActivity 네비게이션 연결
- [x] 선택에 따라 적절한 에디터로 이동
- [x] OCR/편집 모드 자동 감지

**파일 수정**:
- `app/src/main/java/com/example/slideit/ui/components/EditorTypeSelectionDialog.kt` ✅ (신규)
- `app/src/main/java/com/example/slideit/MainActivity.kt` ✅

**완료 시각**: 2025-01-21
**빌드 상태**: ✅ BUILD SUCCESSFUL

---

### Phase 3: 통합 렌더링 시스템 (1일)
**목표**: 모든 명함을 통일된 방식으로 렌더링

- [ ] CardRenderer Composable 생성
- [ ] SimpleCardRenderer 구현 (텍스트 기반 명함)
- [ ] CanvasCardRenderer 구현 (Canvas 기반 명함)
- [ ] 스케일링 로직 구현
- [ ] CardShareScreen에 통합
- [ ] CardStorageScreen에 통합

**파일 수정**:
- `app/src/main/java/com/example/slideit/ui/components/CardRenderer.kt` (신규)
- `app/src/main/java/com/example/slideit/ui/screens/CardShareScreen.kt`
- `app/src/main/java/com/example/slideit/ui/screens/CardStorageScreen.kt`

**진행 상태**: ⏳ 대기 중

---

### Phase 4: 캔버스 에디터 구현 (3-5일)

#### 4.1 기본 캔버스 구조 (1일)
- [ ] CardCanvasEditorScreen 생성
- [ ] Canvas 영역 구현
- [ ] 하단 툴바 UI
- [ ] 상단 바 (저장/취소)

#### 4.2 요소 추가 기능 (1일)
- [ ] 텍스트 추가 다이얼로그
- [ ] 이미지 선택 및 추가
- [ ] 도형 추가 (사각형, 원)
- [ ] 요소 리스트 관리

#### 4.3 제스처 조작 (1-2일)
- [ ] 드래그로 이동
- [ ] 핀치로 크기 조정
- [ ] 회전 제스처
- [ ] 요소 선택/해제

#### 4.4 편집 기능 (1일)
- [ ] 선택된 요소 하이라이트
- [ ] 크기 조정 핸들
- [ ] 속성 편집 패널
- [ ] 레이어 순서 변경
- [ ] 삭제 기능

**파일 생성**:
- `app/src/main/java/com/example/slideit/ui/screens/CardCanvasEditorScreen.kt`
- `app/src/main/java/com/example/slideit/ui/components/CardCanvas.kt`
- `app/src/main/java/com/example/slideit/ui/components/CanvasToolbar.kt`
- `app/src/main/java/com/example/slideit/viewmodel/CanvasEditorViewModel.kt`
- `app/src/main/java/com/example/slideit/util/GestureHandler.kt`

**진행 상태**: ⏳ 대기 중

---

### Phase 5: Bitmap 변환 (1일)
**목표**: 명함을 이미지로 변환하여 공유

- [ ] BitmapConverter 유틸리티 생성
- [ ] SimpleCard → Bitmap 변환
- [ ] CanvasCard → Bitmap 변환
- [ ] 공유 기능 통합
- [ ] 이미지 저장 기능

**파일 생성**:
- `app/src/main/java/com/example/slideit/util/BitmapConverter.kt`

**진행 상태**: ⏳ 대기 중

---

### Phase 6: 테스트 및 통합 (1일)
- [ ] 간편 편집 → 상세 편집 전환 테스트
- [ ] 렌더링 품질 확인
- [ ] 공유 화면 통합 테스트
- [ ] 저장/불러오기 테스트
- [ ] 성능 최적화
- [ ] 버그 수정

**진행 상태**: ⏳ 대기 중

---

## 📊 전체 진행률

```
Phase 1: ✅ 100% [████████████████████████████████████████] (0.5일) 완료!
Phase 2: ✅ 100% [████████████████████████████████████████] (0.5일) 완료!
Phase 3: ⏳ 0%   [........................................] (1일)
Phase 4: ⏳ 0%   [........................................] (3-5일)
Phase 5: ⏳ 0%   [........................................] (1일)
Phase 6: ⏳ 0%   [........................................] (1일)
────────────────────────────────────────────────────────────
전체:    🔄 29%  [███████████.............................] (7-9일)
```

---

## 🛠️ 기술 스택

### 개발 환경
- Kotlin
- Jetpack Compose
- Android Studio

### 주요 라이브러리
- Room (데이터베이스)
- Coil (이미지 로딩)
- ML Kit (OCR)
- Gson (JSON)
- CameraX (카메라)
- DataStore (설정)

---

## 📁 프로젝트 구조

```
app/src/main/java/com/example/slideit/
├── data/
│   ├── model/
│   │   ├── BusinessCard.kt
│   │   └── CardElement.kt (신규)
│   ├── dao/
│   │   └── BusinessCardDao.kt
│   ├── database/
│   │   └── AppDatabase.kt
│   └── repository/
│       └── BusinessCardRepository.kt
├── ui/
│   ├── screens/
│   │   ├── CardEditorScreen.kt (기존)
│   │   ├── CardCanvasEditorScreen.kt (신규)
│   │   ├── CardShareScreen.kt
│   │   └── CardStorageScreen.kt
│   ├── components/
│   │   ├── CardRenderer.kt (신규)
│   │   ├── CardCanvas.kt (신규)
│   │   └── EditorTypeSelectionDialog.kt (신규)
│   └── theme/
│       └── Theme.kt
├── viewmodel/
│   ├── CardViewModel.kt
│   └── CanvasEditorViewModel.kt (신규)
├── util/
│   ├── BitmapConverter.kt (신규)
│   ├── GestureHandler.kt (신규)
│   └── TextRecognitionUtil.kt
└── MainActivity.kt
```

---

## 📝 최근 업데이트

### 2025-01-21
- ✅ 프로젝트 버그 수정 완료
  - Theme.kt Color import 누락 수정
  - CameraScreen.kt deprecated API 수정
  - 빌드 성공 (경고 0개)
- 📋 명함 에디터 대개편 계획 수립
- 📄 README.md 생성 및 작업 계획 문서화
- ✅ **Phase 1 완료**: 데이터 모델 확장
  - CardElement sealed class 생성
  - BusinessCard 모델에 editorType, canvasData, thumbnailPath 추가
  - Database version 3으로 업데이트
  - 빌드 성공 확인
- ✅ **Phase 2 완료**: 에디터 선택 화면
  - EditorTypeSelectionDialog 컴포넌트 생성
  - 그라데이션 카드 UI로 간편/상세 편집 옵션 표시
  - MainActivity 네비게이션 통합
  - 빌드 성공 확인

---

## 🎯 다음 단계
1. Phase 3 시작: 통합 카드 렌더링 시스템 구현
2. CardRenderer Composable 생성
3. CardShareScreen에 통합

---

## 📞 문의
프로젝트 관련 문의사항은 이슈를 통해 남겨주세요.
