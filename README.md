# Receipt Scanner — Mobile App (Android + iOS)

A Compose Multiplatform app that lets a user photograph a receipt, review AI-extracted data, and save it — built against the [Receipt Scanner backend](https://github.com/bilal96aslam/receipt-scanner).

**Backend repo:** https://github.com/bilal96aslam/receipt-scanner
**Live backend API:** https://receipt-scanner-uk8c.onrender.com

## What it does

1. Capture a receipt photo via camera or pick one from the gallery.
2. The image is uploaded to the backend, which runs it through Gemini's vision API and returns extracted vendor, date, and total.
3. Review screen shows the extracted data as editable fields — nothing is saved yet.
4. On confirm, the data is saved to the backend's database. On discard, nothing is saved.
5. A history screen lists all previously confirmed receipts, loaded live from the backend.

## Why one codebase for Android and iOS

Built with Kotlin Multiplatform and Compose Multiplatform instead of separate native codebases. UI, business logic, networking, and state management are shared in `commonMain`; only camera/gallery access is platform-specific (`androidMain`/`iosMain`), since that's the one piece each OS handles differently.

## Architecture

- **Pattern:** MVVM — ViewModel + StateFlow for UI state, sealed classes for Idle/Loading/Success/Error states
- **Networking:** Ktor client, with a thin Repository layer wrapping API calls in `Result<T>`
- **Serialization:** kotlinx.serialization, with typed request/response models matching the backend's schema exactly
- **Image capture:** `expect`/`actual` pattern — `ImagePicker.android.kt` uses `FileProvider` + `ActivityResultContracts`, `ImagePicker.ios.kt` is the iOS counterpart

## Screens

- **Scan** — camera/gallery capture, upload, loading and error states
- **Review** — editable vendor/date/total fields, a warning banner when the backend flags a receipt as `needs_review`, Confirm/Discard actions
- **History** — list of all confirmed receipts, pulled from the backend on screen entry

## Tech stack

- Kotlin Multiplatform / Compose Multiplatform
- Ktor Client (networking)
- kotlinx.serialization
- MVVM (ViewModel, StateFlow)

## Running locally

```bash
git clone https://github.com/bilal96aslam/ReceiptScanner
```

Open the project in Android Studio.

In `commonMain/kotlin/data/ApiClient.kt`, set:

```
BASE_URL = "https://receipt-scanner-uk8c.onrender.com"
API_KEY = "your_api_secret_key_here"
```

Run on an emulator or physical device from Android Studio. For iOS, open the project in Xcode via the generated KMP target and run on a simulator.

## Connecting to the backend

This app expects the backend's API contract exactly — `/scan` for extraction, `/receipts` for save-on-confirm, `/receipts` (GET) for history. See the [backend repo](https://github.com/bilal96aslam/receipt-scanner) for the full API reference and how to run your own backend instance instead of pointing at the live one.

## What's not included (by design)

No Play Store listing, closed testing, or release signing assets — this project focused on a working, deployed product rather than a public store release.
