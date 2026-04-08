# KyTucXaApp - Hệ Thống Ứng dụng Ký Túc Xá Thông Minh

Ứng dụng di động giúp đăng ký lưu trú tại ký túc xá và sử dụng các dịnh vụ của ký túc xá, được xây dựng trên nền tảng Android.

## 1. Công nghệ sử dụng

Các công nghệ sử dụng trong dự án:

*   **Ngôn ngữ:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Khai báo giao diện hiện đại)
*   **Kiến trúc:** Clean Architecture & MVVM (Model-View-ViewModel)
*   **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
*   **Mạng (Networking):** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
*   **Xử lý JSON:** [Moshi](https://github.com/square/moshi)
*   **Xử lý hình ảnh:** [Coil](https://coil-kt.github.io/coil/)

## 2. Tính năng nổi bật (Chưa hoàn thiện)

## 3. Cấu trúc dự án

Dự án được chia theo các package chính:

*   `core/`: Chứa các thành phần dùng chung (Utils, UI Components, Base classes).
*   `data/`: Chứa implementations của repository, API service (Retrofit), DTOs và Local Database (Room).
*   `domain/`: Chứa Business Logic, Model và Interface Repository.
*   `feature/`: Chứa các màn hình UI và ViewModel được chia theo từng tính năng cụ thể.


