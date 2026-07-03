# Deliveries

Deliveries is an Android application that lets you track your parcels from various providers with ease.

## Features

- **Multi-Courier Tracking**: Integrated with the Binderbyte API to track a wide range of Indonesian shipping carriers.
- **Rich Package Information**: Displays extensive parcel details directly from the API response:
  - Last updated timestamp.
  - Package description (replaces user-defined title dynamically if returned by the carrier).
  - Origin and Destination cities.
  - Shipper (Sender) and Receiver names.
  - Chronological tracking history with localized dates and times.
- **Courier-Specific Validation**: Adapts dynamically to carrier requirements. For example, JNE tracking automatically enables and mandates entering the last 5 digits of the recipient's phone number as verification.
- **Privacy & Security Focused**:
  - Excludes sensitive HTTP network logs in production builds.
  - Redacts PII (like tracking IDs and phone numbers) from device system logs (Logcat).
  - Disables application backups to prevent unauthorized extraction of the stored API key and parcel logs.

## Contributing

We use `ktfmt` for formatting files. For ease of use, we included the sample editorconfig that comes with `ktfmt`, as well as a helper script to invoke it.
To format all the code, simply run `./scripts/ktfmt.sh .`. It will download `ktfmt` if necessary.

Similarly, we have `./scripts/sort-strings.sh` to sort translation files by key. This script uses Nix to pull in `xsltproc` from `libxslt`.

## Supported services

This app exclusively uses the [Binderbyte API](https://binderbyte.com/) for tracking and requires users to manually input their own API Key in the Settings menu.

Indonesia (Binderbyte API):

- JNE (Requires recipient phone number verification)
- POS Indonesia
- J&T Express
- J&T Cargo
- SiCepat
- TIKI
- AnterAja
- Wahana
- Ninja Xpress
- Lion Parcel
- PCP Express
- JET Express
- REX Express
- First Logistics
- ID Express
- Shopee Express
- KGXpress
- SAP Express
- JX Express
- RPX
- Lazada eLogistics
- Indah Cargo
- Dakota Cargo
- Kurir Rekomendasi Tokopedia
