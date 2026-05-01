🌾 RiceScan – Rice Disease Detection App

RiceScan is an Android application that uses machine learning to detect diseases in rice plants from images. The app helps farmers and agricultural practitioners identify plant diseases early and provides useful information such as symptoms, prevention, and treatment.

📱 Features
📷 Real-time Image Capture
Capture images of rice plants using the device camera
🤖 AI-Based Disease Detection
Uses a machine learning model to classify rice plant diseases
📊 Confidence Visualization
Displays prediction confidence with a visual indicator
📚 Disease Information
Detailed insights including:
Symptoms
Prevention methods
Treatment options
🕓 Detection History
Saves previous scans locally using Room database
View past detections in “My Plants” section
🎨 Modern UI
Clean and user-friendly interface with Material design elements
🏗️ Project Structure
Production-Project/
│
├── app/
│   ├── src/main/
│   │   ├── java/com/example/ricescan/
│   │   │   ├── camera/           # Camera functionality
│   │   │   ├── detail/           # Disease detail screens
│   │   │   ├── history/          # Local database & history
│   │   │   ├── home/             # Home screen
│   │   │   ├── ml/               # ML model & classification logic
│   │   │   ├── result/           # Result display UI
│   │   │   ├── ui/theme/         # App theming
│   │   │   ├── MainActivity.kt
│   │   │   └── SplashActivity.kt
│   │   ├── res/                 # Layouts, drawables, etc.
│   │   └── AndroidManifest.xml
│
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
🧠 How It Works
User captures or selects an image of a rice plant
The image is passed to the DiseaseClassifier
The ML model processes the image and predicts the disease
Results are displayed along with confidence score
Detection is saved into local database for future reference
🛠️ Tech Stack
Language: Kotlin
Architecture: Fragment-based Android architecture
Database: Room (SQLite)
Machine Learning: TensorFlow Lite (inferred from classifier structure)
UI: Android Views + Custom Views
Build System: Gradle (Kotlin DSL)
🚀 Getting Started
Prerequisites
Android Studio (latest version recommended)
Android SDK installed
Physical device or emulator
Installation

Clone the repository:

git clone https://github.com/your-username/ricescan.git
Open the project in Android Studio

Sync Gradle:

File → Sync Project with Gradle Files

Run the app:

Run → Run 'app'
📂 Key Components
DiseaseClassifier.kt
Core ML inference logic
DiseaseRepository.kt
Provides disease-related data
CameraFragment.kt
Handles camera input and image capture
ResultFragment.kt
Displays prediction results
AppDatabase.kt
Room database configuration
🔒 Permissions Required
Camera access
Storage (if saving images)
📌 Future Improvements
🌐 Cloud-based model updates
📈 More disease categories
🌍 Multi-language support
📊 Analytics dashboard for farmers
☁️ Cloud backup for history
🤝 Contributing

Contributions are welcome!

Fork the repo
Create a new branch
Make your changes
Submit a pull request
📄 License

This project is licensed under the MIT License – feel free to use and modify.

👨‍💻 Author

Developed as part of a production-level Android project focused on agricultural technology.
