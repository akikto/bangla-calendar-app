# বাঙালি ক্যালেন্ডার — Android Studio ছাড়াই বিল্ড করে Play Store এ পাবলিশ

এই app এ আছে:
- **App এর মূল স্ক্রিন**: তোমার পুরো HTML ক্যালেন্ডার (WebView দিয়ে)
- **দুটো native home-screen widget**: Daily (ছোট) ও Monthly (বড়, prev/next বাটন সহ)

Android Studio ইনস্টল না করেই, GitHub Actions ব্যবহার করে ক্লাউডে এটা বিল্ড করা যাবে। নিচের ধাপগুলো অনুসরণ করো।

## তাড়াতাড়ি টেস্ট করতে চাইলে (signing ছাড়াই)

Keystore/secrets কিছু সেট না করেই, শুধু repo push করার পরই **"Build APK"** workflow (Actions ট্যাবে) automatic চলে একটা unsigned **debug APK** বানিয়ে দেয় — এটা ফোনে ইনস্টল করে app + widget ঠিকমতো কাজ করছে কিনা তাড়াতাড়ি দেখে নেওয়া যায়, Play Store এ আপলোডের আগে। এই APK দিয়ে Play Store এ আপলোড করা যাবে না (তার জন্য নিচের ধাপ ২-৫ লাগবে, signed AAB দরকার)।

## ধাপ ১: GitHub এ আপলোড করো

1. GitHub এ নতুন একটা repository বানাও (public বা private, দুটোই চলবে) — যেমন `bangla-calendar-app`।
2. এই পুরো ফোল্ডারের সব ফাইল ও ফোল্ডার (`.github` ফোল্ডার সহ, এটা hidden ফোল্ডার — লুকিয়ে না যায় খেয়াল রেখো) সেই repo তে আপলোড/পুশ করো।
   - সহজ উপায়: GitHub ওয়েবসাইটে repo খুলে **Add file → Upload files**, পুরো ফোল্ডার ড্র্যাগ করে দাও। (`.github` ফোল্ডারটা আলাদাভাবে আপলোড করতে হতে পারে, কারণ কিছু browser hidden ফোল্ডার আলাদা করে দেখায় না — চাইলে `git` কমান্ড লাইন দিয়েও করতে পারো)।

## ধাপ ২: একটা signing keystore বানাও (একবারই করতে হবে)

Play Store এ app আপলোড করতে একটা "signing key" লাগে। Android Studio বা লোকাল Java ছাড়াই এটা GitHub Actions দিয়ে বানানো যায়:

1. তোমার repo তে **Actions** ট্যাবে যাও।
2. বাম দিকে **"Generate Release Keystore"** workflow এ ক্লিক করো।
3. **"Run workflow"** বাটনে ক্লিক করো — কয়েকটা ফিল্ড আসবে (alias, password ইত্যাদি), নিজের মতো পূরণ করো (পাসওয়ার্ডগুলো মনে রাখো/লিখে রাখো, পরে লাগবে)।
4. Run শেষ হলে (১ মিনিটের মতো লাগবে), সেই run এর পেজে নিচে **Artifacts** সেকশনে `release-keystore` নামে একটা zip পাবে — ডাউনলোড করো।
5. Zip এর ভেতরে দুটো ফাইল: `release.keystore` (এটা কম্পিউটারে নিরাপদে রাখো, হারালে ভবিষ্যতে app আপডেট করা যাবে না) আর `release.keystore.base64.txt` (এটার ভেতরের পুরো টেক্সট কপি করবে, পরের ধাপে লাগবে)।

## ধাপ ৩: Secrets যোগ করো

1. Repo তে **Settings → Secrets and variables → Actions** এ যাও।
2. **"New repository secret"** দিয়ে এই ৪টা যোগ করো:
   - `KEYSTORE_BASE64` → `release.keystore.base64.txt` ফাইলের ভেতরের পুরো টেক্সট পেস্ট করো
   - `KEYSTORE_PASSWORD` → ধাপ ২ তে দেওয়া store password
   - `KEY_ALIAS` → ধাপ ২ তে দেওয়া alias
   - `KEY_PASSWORD` → ধাপ ২ তে দেওয়া key password

## ধাপ ৪: App বিল্ড করো

1. Actions ট্যাবে **"Build Signed Release (AAB + APK)"** workflow এ যাও।
2. **"Run workflow"** চাপো (অথবা `main` branch এ কিছু commit/push করলেই এটা automatic চলবে)।
3. ২-৪ মিনিট লাগবে। শেষ হলে সেই run এর পেজে দুটো artifact পাবে:
   - `app-release-aab` → এটা **Play Store এ আপলোড করার ফাইল**
   - `app-release-apk` → এটা তোমার নিজের ফোনে ইনস্টল করে টেস্ট করার ফাইল (widget সহ সবকিছু ঠিকমতো কাজ করছে কিনা দেখার জন্য)

> টেস্ট করার পরামর্শ: প্রথমে APK ফোনে ইনস্টল করে (Settings এ "Install unknown apps" অনুমতি দিতে হতে পারে) app + দুটো widget ঠিকমতো কাজ করছে কিনা দেখে নাও, তারপর AAB Play Store এ আপলোড করো।

## ধাপ ৫: Play Store এ আপলোড করো

1. [Google Play Console](https://play.google.com/console) এ developer অ্যাকাউন্ট বানাও (এককালীন $25 fee)।
2. নতুন app তৈরি করো, প্রয়োজনীয় তথ্য (নাম, description, screenshots, privacy policy, content rating) পূরণ করো।
3. **Production → Create new release** এ গিয়ে `app-release.aab` ফাইলটা আপলোড করো, submit করো।

## পরে কোড বদলালে

শুধু ধাপ ৪ আবার করলেই হবে (নতুন `.aab` পাবে) — নতুন keystore বানানোর দরকার নেই, ধাপ ২-৩ একবারই করতে হয়। নতুন version আপলোড করার আগে `app/build.gradle` এ `versionCode` এক করে বাড়িয়ে দিও (Play Store একই versionCode দুবার নেয় না)।

## ফাইল স্ট্রাকচার

```
.github/workflows/
├── build-apk.yml           → দ্রুত unsigned debug APK (শুধু টেস্টের জন্য, signing লাগে না)
├── generate-keystore.yml   → signing key বানানোর workflow (একবার চালাবে)
└── build-release.yml       → signed AAB/APK বিল্ড করার workflow (বারবার চালানো যাবে)
app/src/main/
├── AndroidManifest.xml     → MainActivity (launcher) + দুটো widget receiver
├── assets/index.html       → তোমার পুরো HTML ক্যালেন্ডার
├── java/com/bnwidget/calendar/
│   ├── MainActivity.kt         → WebView দিয়ে index.html লোড করে
│   ├── BengaliDateUtils.kt     → বাংলা তারিখ হিসাব + ২০২৬ এর ছুটির লিস্ট
│   ├── DailyWidgetProvider.kt  → ছোট widget
│   └── MonthlyWidgetProvider.kt→ বড় widget (prev/next বাটন সহ)
├── res/layout/, res/xml/, res/drawable/, res/values/ → UI ও metadata
```

## সীমাবদ্ধতা

- বড় widget এ কোনো দিনে ট্যাপ করলে event এর বিস্তারিত মডাল দেখানো নেই — শুধু রঙিন বিন্দু (●) দিয়ে বোঝানো হয়।
- `index.html` এর ইভেন্ট ডেটা আর widget এর `BengaliDateUtils.kt` এর ইভেন্ট ডেটা আলাদা কপি — নতুন ছুটি যোগ করলে দুই জায়গায় করতে হবে যদি দুই জায়গাতেই দেখাতে চাও।
