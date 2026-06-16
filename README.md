# 🎬 Video Editor Pro

<p align="center">
  <img src="android/app/src/main/res/drawable/ic_launcher_foreground.xml" width="120" height="120" alt="Video Editor Pro Icon">
</p>

<p align="center">
  <a href="https://android.com"><img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Language-Kotlin-blue?style=flat-square&logo=kotlin"></a>
  <a href="https://github.com/ffmpeg-kit/ffmpeg-kit"><img src="https://img.shields.io/badge/FFmpeg-Kit%206.0-FF5097?style=flat-square"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-orange?style=flat-square"></a>
</p>

> A powerful yet lightweight video editing application for Android, built with modern architecture and powered by FFmpegKit.

---

## ✨ Key Features

### 🎥 Video Editing
| Feature | Description |
|---------|-------------|
| **Video Import** | Support for MP4, MOV, AVI, MKV, WebM formats |
| **Clip Trimming** | Precision trimming with millisecond accuracy |
| **Clip Merging** | Seamlessly combine multiple video clips |
| **Speed Control** | Adjust playback speed from 0.25x to 4x |
| **Rotation** | Rotate videos 90°, 180°, or 270° |
| **Flip** | Horizontal and vertical flip options |

### 🎵 Audio Management
| Feature | Description |
|---------|-------------|
| **Audio Tracks** | Add multiple audio tracks |
| **Volume Control** | Individual volume per track with fade in/out |
| **Audio Mixing** | Mix original and added audio |

### 📝 Subtitles
| Feature | Description |
|---------|-------------|
| **Subtitle Editor** | Add and edit subtitles with timing |
| **Burn Subtitles** | Hardcode subtitles into video |
| **Multiple Formats** | Support for SRT and ASS formats |

### 🎨 Visual Effects
| Effect | Description |
|--------|-------------|
| Brightness | Adjust video brightness (-100% to +100%) |
| Contrast | Enhance or reduce contrast |
| Saturation | Control color intensity |
| Warmth | Add warm or cool tones |
| Vignette | Cinematic edge darkening |
| Sepia | Vintage film look |
| Grayscale | Black and white conversion |
| Blur | Background blur effect |

### 📤 Export Options
| Quality | Resolution | Bitrate |
|---------|------------|---------|
| **Low** | 480p (854×480) | 4 Mbps |
| **Medium** | 720p (1280×720) | 8 Mbps |
| **High** | 1080p (1920×1080) | 15 Mbps |
| **Ultra** | 4K (3840×2160) | 45 Mbps |

**Formats:** MP4 (H.264), WebM (VP9), GIF

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Activities │  │ ViewModels  │  │     Adapters        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                        Domain Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Models    │  │  Use Cases  │  │    Repositories     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                         Data Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │    Room     │  │   Media     │  │  Repository Impl    │  │
│  │  Database   │  │ Repository  │  └─────────────────────┘  │
│  └─────────────┘  └─────────────┘                            │
├─────────────────────────────────────────────────────────────┤
│                        Core Layer                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ FFmpegKit   │  │  Utilities  │  │     Constants       │  │
│  │  Service    │  └─────────────┘  └─────────────────────┘  │
│  └─────────────┘                                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 1.9.20 |
| **Min SDK** | Android 7.0 (API 24) |
| **Target SDK** | Android 14 (API 34) |
| **Architecture** | Clean Architecture + MVVM |
| **DI** | Hilt 2.50 |
| **Database** | Room 2.6.1 |
| **Video Processing** | FFmpegKit 6.0 |
| **Video Playback** | ExoPlayer (Media3) 1.2.1 |
| **Image Loading** | Glide 4.16.0 |
| **Async** | Kotlin Coroutines + Flow |
| **UI** | Material Design 3 |

---

## 🎨 Design System

### Color Palette

```
┌─────────────────────────────────────────────────────────────┐
│                     Primary Colors                           │
├─────────────┬─────────────┬──────────────────────────────────┤
│   Primary   │ #7C4DFF     │  ████████████████████████████    │
│   Variant   │ #651FFF     │  ████████████████████████████    │
│   Secondary │ #00E5FF     │  ████████████████████████████    │
├─────────────┴─────────────┴──────────────────────────────────┤
│                    Background Colors                         │
├─────────────┬─────────────┬──────────────────────────────────┤
│   Background│ #121212     │  ████████████████████████████    │
│   Surface   │ #1E1E1E     │  ████████████████████████████    │
│   Surface+  │ #2D2D2D     │  ████████████████████████████    │
└─────────────┴─────────────┴──────────────────────────────────┘
```

### Typography

| Style | Font | Size | Weight |
|-------|------|------|--------|
| Headline | System | 28sp | Medium |
| Title | System | 22sp | Medium |
| Subtitle | System | 16sp | Regular |
| Body | System | 14sp | Regular |
| Caption | System | 12sp | Regular |

---

## 📱 Project Structure

```
VideoEditorApp/
├── .github/
│   └── workflows/
│       ├── build-android.yml     # Debug build CI
│       └── release.yml           # Release automation
├── android/
│   ├── app/
│   │   ├── build.gradle
│   │   └── src/main/
│   │       ├── java/com/videoeditor/app/
│   │       │   ├── core/          # FFmpeg, utils, constants
│   │       │   ├── data/          # Room, repositories
│   │       │   ├── di/            # Hilt modules
│   │       │   ├── domain/        # Models, use cases
│   │       │   └── presentation/  # UI layer
│   │       └── res/               # Resources
│   ├── build.gradle
│   └── gradle/
├── build.gradle
├── gradle.properties
├── settings.gradle
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 17
- Android SDK 34
- Gradle 8.2

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/VideoEditorApp.git
   cd VideoEditorApp
   ```

2. **Build the debug APK**
   ```bash
   cd android
   chmod +x gradlew
   ./gradlew assembleDebug
   ```

3. **Install on device**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Building Release APK

1. Configure signing in `android/app/build.gradle`:
   ```gradle
   signingConfigs {
       release {
           storeFile file('your-keystore.jks')
           storePassword 'your-password'
           keyAlias 'your-alias'
           keyPassword 'your-key-password'
       }
   }
   ```

2. Build release:
   ```bash
   ./gradlew assembleRelease
   ```

---

## 📂 Key Classes

### Core FFmpeg Module
| Class | Purpose |
|-------|---------|
| `FFmpegService` | Execute FFmpeg commands |
| `VideoProcessor` | Video trimming, scaling, encoding |
| `AudioProcessor` | Audio mixing and volume control |
| `SubtitleProcessor` | Generate SRT/ASS files |
| `EffectProcessor` | Apply video filters |
| `RenderEngine` | Complete render pipeline |

### Domain Layer
| Class | Purpose |
|-------|---------|
| `Project` | Video project entity |
| `VideoClip` | Video clip with metadata |
| `AudioTrack` | Audio track entity |
| `Subtitle` | Subtitle entity |
| `Effect` | Video effect model |
| `ExportSettings` | Export configuration |

---

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 👨‍💻 Author / Admin

| Field | Information |
|-------|-------------|
| **Deploy Name** | AmkyawDev |
| **Admin** | Aung Myo Kyaw |
| **Country** | Myanmar 🇲🇲 |
| **Location** | Naypyidaw |
| **Phone** | 09677740154 |
| **TikTok** | [@amkyaw.dev1](https://tiktok.com/@amkyaw.dev1) |

---

## 📧 Contact

For questions and support, please contact:

- **GitHub:** [@amkyawdev](https://github.com/amkyawdev)
- **TikTok:** [@amkyaw.dev1](https://tiktok.com/@amkyaw.dev1)
- **Phone:** 09677740154

---

<p align="center">
  <strong>© 2024 AmkyawDev | Made with ❤️ and Kotlin</strong>
</p> 
