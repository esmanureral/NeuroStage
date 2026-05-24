<div align="center">

<img src="docs/images/logo.png" alt="NeuroStage" width="128" height="128"/>

  <img src="afis.png" alt="NeuroStage Poster" width="700"/>

**Akıllı MR analizi · Grad-CAM açıklanabilirlik · LLM klinik rapor · Evreye uygun bilişsel takip**

Android uygulaması — Jetpack Compose · Cihaz üzerinde ML · Firebase

<br/>

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.12-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Auth%20%2B%20Firestore-FFCA28?style=flat-square&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![LiteRT](https://img.shields.io/badge/LiteRT-1.4.1-FF6F00?style=flat-square&logo=tensorflow&logoColor=white)](https://ai.google.dev/edge/litert)
[![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/)
[![targetSdk](https://img.shields.io/badge/targetSdk-35-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/)

</div>

---

## Amacımız

**NeuroStage**, aksiyel beyin MRI görüntülerini **cihaz üzerinde** dört klinik evreye göre sınıflandırır; sonuçları **Firestore**'da saklar; hekim tarafında **Grad-CAM** ve **yapay zekâ destekli klinik özet** sunar. Hasta tarafında ise tarama sonucuna göre **anlaşılır bilgilendirme**, **bilişsel egzersizler** ve **hatırlatıcılar** ile günlük takibi destekler.

| Persona | Odak |
|---------|------|
| **Doktor** | Hasta yönetimi, MR analizi, XAI, AI rapor, geçmiş ve karşılaştırma |
| **Hasta** | MR tarama, sade sonuç, evreye göre program hub, oyunlar, hatırlatıcılar |

> **Önemli:** Uygulama ön değerlendirme ve karar **destek** aracıdır; kesin tanı veya tedavi planı yerine geçmez. Klinik kararlar mutlaka uzman hekim tarafından verilmelidir.

---

## Ekranlar

### 👨‍⚕️ Doktor

<table>
  <tr>
    <td align="center" width="20%">
      <img src="docs/images/doctor/02-dashboard.png" width="220"/>
    </td>
    <td align="center" width="20%">
      <img src="docs/images/doctor/03-patients.png" width="220"/>
    </td>
    <td align="center" width="20%">
      <img src="docs/images/doctor/04-intake.png" width="220"/>
    </td>
    <td align="center" width="20%">
      <img src="docs/images/doctor/05-patient-history.png" width="220"/>
    </td>
    <td align="center" width="20%">
      <img src="docs/images/doctor/06-scan.png" width="220"/>
    </td>
  </tr>
  <tr>
    <td align="center" width="20%">
      <img src="docs/images/doctor/07-analysis.png" width="220"/>
    </td>
    <td align="center" width="20%">
      <img src="docs/images/doctor/08-gradcam.png" width="220"/>
    </td>
    <td align="center" width="20%">
      <img src="docs/images/doctor/09-ai-report.png" width="220"/>
    </td>
    <td align="center" width="20%">
      <img src="docs/images/doctor/10-result-detail.png" width="220"/>
    </td>
    <td align="center" width="20%"></td>
  </tr>
</table>

### 👤 Hasta

<table>
  <tr>
    <td align="center" width="25%">
      <img src="docs/images/patient/01-role-pick.png" width="250"/>
    </td>
    <td align="center" width="25%">
      <img src="docs/images/patient/02-scan.png" width="250"/>
    </td>
    <td align="center" width="25%">
      <img src="docs/images/patient/03-scan-result.png" width="250"/>
    </td>
    <td align="center" width="25%">
      <img src="docs/images/patient/04-home.png" width="250"/>
    </td>
  </tr>
  <tr>
    <td align="center" width="25%">
      <img src="docs/images/patient/05-program-hub.png" width="250"/>
    </td>
    <td align="center" width="25%">
      <img src="docs/images/patient/06-exercises.png" width="250"/>
    </td>
    <td align="center" width="25%">
      <img src="docs/images/patient/07-reminders.png" width="250"/>
    </td>
    <td align="center" width="25%">
      <img src="docs/images/patient/08-routine-game.png" width="250"/>
    </td>
  </tr>
  <tr>
    <td align="center" width="25%">
      <img src="docs/images/patient/09-memory-game.png" width="250"/>
    </td>
    <td align="center" width="25%">
      <img src="docs/images/patient/10-puzzle.png" width="250"/>
    </td>
    <td align="center" width="25%">
      <img src="docs/images/patient/11-color-match.png" width="250"/>
    </td>
    <td align="center" width="25%"></td>
  </tr>
</table>

---

## Özellikler

### Doktor

- Firebase Auth ile güvenli giriş
- Hasta kayıt, listeleme ve silme
- MRI yükleme (galeri / kamera), **MRI filtresi** ile geçersiz görüntüleri eleme
- **LiteRT** ile 4 sınıf sınıflandırma (cihazda, gecikmesiz)
- Sınıf olasılıkları, güven skoru, hasta geçmişi zaman çizelgesi
- **Grad-CAM** (Hugging Face Space / özel API) ile model odağı
- **Groq (Llama)** ve **Gemini** ile Türkçe klinik özet rapor
- Tüm taramalar arası geçmiş ve detay ekranı

### Hasta

- Rol seçimi sonrası anonim MR tarama akışı
- Evreye göre **sade Türkçe** sonuç ve eğitim kartları
- Demans bulgusu yok → ana sayfa; hafif/orta evre → **program hub**
- Bilişsel egzersizler: rutin sırası, hafıza, yapboz, eşleştirme, renk oyunu
- Evreye göre zorluk (kart sayısı, ızgara boyutu, route guard)
- Yerel **hatırlatıcılar** (ilaç, randevu vb.)
- Tarama geçmişi ve detay (isteğe bağlı ekran görüntüsü)

### Sınıflandırma evreleri

| İndeks | Etiket |
|--------|--------|
| 0 | Hafif evre demans |
| 1 | Orta evre demans |
| 2 | Demans bulgusu yok |
| 3 | Çok hafif evre demans |

---

## Mimari

```mermaid
flowchart TB
    subgraph UI["Sunum katmanı"]
        Compose["Jetpack Compose · Material 3"]
        Nav["Navigation Compose"]
        VM["ViewModel"]
    end

    subgraph Domain["İş mantığı"]
        Stage["PatientStage · route guard"]
        Repo["Repository arayüzleri"]
    end

    subgraph Data["Veri"]
        Prefs["AppPreferences"]
        FS["Firestore · Auth"]
        ML["LiteRT · .tflite"]
        XAI["Grad-CAM API · Groq/Gemini"]
    end

    Compose --> Nav --> VM --> Repo
    Repo --> Prefs
    Repo --> FS
    Repo --> ML
    Repo --> XAI
```

```
┌─────────────────────────────────────────────────────────────┐
│  Compose UI · Material 3 · Hasta / Doktor temaları          │
├─────────────────────────────────────────────────────────────┤
│  Navigation · ViewModel · Hilt DI                           │
├─────────────────────────────────────────────────────────────┤
│  Repository: patients · scans · profile · xai               │
├─────────────────────────────────────────────────────────────┤
│  LiteRT (MRI filtre + Alzheimer) │ Retrofit · OkHttp · Coil │
├─────────────────────────────────────────────────────────────┤
│  Firebase Auth · Firestore (users/{uid}/patients/scans)     │
└─────────────────────────────────────────────────────────────┘
```

| Katman | Teknoloji |
|--------|-----------|
| UI | Jetpack Compose BOM `2024.12.01`, Material 3 |
| Mimari | MVVM · tek yönlü state · Repository |
| DI | Hilt `2.51.1` · KSP |
| Navigasyon | Navigation Compose `2.8.5` |
| ML | Google LiteRT `1.4.1` · `assets/*.tflite` |
| XAI | Grad-CAM (HF Gradio / REST) · Groq · Gemini |
| Backend | Firebase BOM `33.16.0` · Auth · Firestore |
| Ağ | OkHttp `4.12` · Retrofit `2.9` · Coil `2.4` |


---

## ML & Analiz Pipeline

```mermaid
flowchart LR
    A["📷 MRI girişi"] --> B{"MRI filtresi\nmri_filter_v2"}
    B -->|Geçersiz| Z["Kullanıcı uyarısı"]
    B -->|Geçerli| C["Alzheimer sınıflandırıcı\nint8 · 260px"]
    C --> D["Firestore kayıt"]
    C --> E["Grad-CAM"]
    C --> F["LLM klinik rapor"]
    E --> G["Doktor UI"]
    F --> G
    D --> H["Geçmiş & karşılaştırma"]
    C --> I["Hasta evre & hub"]
```

| Model | Dosya | Görev |
|-------|-------|-------|
| MRI filtresi | `mri_filter_v2_noquant.tflite` | Aksiyel olmayan / geçersiz girişleri ayıklar (224×224) |
| Alzheimer | `alzheimer_preprocessed_int8_floatio.tflite` | 4 evre sınıflandırması (260×260, INT8) |

Uzak servisler (API anahtarı `local.properties`):

| Servis | Amaç |
|--------|------|
| Grad-CAM Space | Isı haritası, anatomik odak bölgesi |
| Groq (`GROK_API_KEY`) | Hızlı klinik özet (Llama) |
| Gemini (`GEMINI_API_KEY`) | Alternatif / ek rapor üretimi |

---

## Teknoloji Özeti 

| Alan | Neler yapıldı |
|------|---------------|
| **Android** | Compose, MVVM, Navigation, Hilt, Material 3, çok persona |
| **Edge ML** | LiteRT, çok modelli pipeline, MRI ön filtresi |
| **XAI** | Grad-CAM entegrasyonu, HF Space uyandırma / retry |
| **LLM** | Klinik rapor prompt'u, Markdown parse, hasta bağlamı |
| **Backend** | Firebase Auth, Firestore güvenlik kuralları, scan geçmişi |
| **Ürün** | Evreye duyarlı hasta UX, Play 16 KB page size, gizli anahtar ayrımı |

---

## Kurulum

### Adımlar

```bash
git clone <repo-url>
cd NeuroStage
copy local.properties.example local.properties   # Windows
# cp local.properties.example local.properties   # macOS / Linux
```

`local.properties` içine ekleyin:

| Anahtar | Açıklama |
|---------|----------|
| `sdk.dir` | Android SDK yolu |
| `GROK_API_KEY` | Groq API (klinik rapor) |
| `GEMINI_API_KEY` | Google Gemini (isteğe bağlı) |
| `GRAD_CAM_API_BASE_URL` | Grad-CAM API tabanı (varsayılan HF Space) |
| `HF_TOKEN` | Özel HF Space için okuma token'ı |


```bash
./gradlew assembleDebug      # geliştirme
./gradlew bundleRelease      # Play Store AAB
```

---

## Proje Yapısı

```
NeuroStage/
├── app/
│   ├── src/main/
│   │   ├── assets/              # .tflite modelleri
│   │   ├── java/.../neurostage/
│   │   │   ├── ui/              # Compose ekranları (doctor, patient, onboarding)
│   │   │   ├── xai/             # Grad-CAM, LLM rapor
│   │   │   ├── patients/        # Firestore hasta repo
│   │   │   ├── scans/           # Tarama modelleri & repo
│   │   │   ├── domain/          # PatientStage, iş kuralları
│   │   │   └── di/              # Hilt modülleri
│   │   └── res/
│   └── build.gradle.kts
├── docs/images/
│   ├── logo.png
│   ├── doctor/                  # 01–10.png
│   ├── patient/                 # 01–11.png
│   └── EKRAN_GORUNTULERI.md
├── scripts/                     # gradcam_api.py, gradcam_hf_app.py
├── firestore.rules
├── app_icon.svg
└── README.md
```

---

## Lisans ve İletişim

Bu depo kişisel / portföy projesidir. Kullanım ve lisans için depo sahibiyle iletişime geçin.

📧 esmanur2eral@gmail.com

---

<div align="center">

**NeuroStage** — Nörolojik evreyi anlamak, klinik kararı desteklemek, hastayı günlük hayatta güçlendirmek.

<br/>

<img src="Esmanur ERAL-poster (1).png" alt="NeuroStage Poster" width="700"/>

</div>
