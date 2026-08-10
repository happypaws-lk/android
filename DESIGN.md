# Happy Paws Android - Design Guidelines

This document serves as the single source of truth for the design constraints, typography, colors, and styling rules used throughout the `happypaws-android` app. All new UI screens and components must strictly adhere to these rules.

## 1. Typography

We use the **Outfit** font family across the entire application to maintain a modern, friendly, and clean aesthetic.

- **Titles / Headers** (`headlineMedium` and similar large text): Use **Outfit SemiBold**.
- **Body / Descriptions** (`bodyMedium`, `bodyLarge`): Use **Outfit Regular**.
- **Buttons / Labels** (`labelLarge`): Use **Outfit SemiBold**.

The font is integrated via Jetpack Compose's `ui-text-google-fonts` Downloadable Fonts system. If you create new `TextStyle` entries in `Type.kt`, always ensure they utilize `OutfitFontFamily` with the appropriate `FontWeight`.

## 2. Color Palette

The application strictly utilizes a Light Theme. Dark Theme is currently not supported and dynamic colors should not override the branding colors.

- **Primary Accent** (`MaterialTheme.colorScheme.primary`): `#008585` (Deep Teal)
- **Main Text / Neutral 20** (`MaterialTheme.colorScheme.onBackground`, `onSurface`): `#303036` (Dark Charcoal)
- **Faded Text / Neutral 60** (`Neutral60` in `Color.kt`): `#99303036` (~60% opacity of Neutral 20)
- **Backgrounds** (`background`, `surface`): White (`#FFFFFF`)

## 3. UI Patterns

- **Onboarding / Pagers**: Use `HorizontalPager` to group related sliding content while keeping primary navigation (like "Continue" or "Sign in" buttons) static at the bottom of the screen. This reduces layout duplication and provides native gesture support.
- **Buttons**: Primary call-to-action buttons should be filled with the Primary Accent (`#008585`) and feature rounded corners (e.g., `8.dp`). The text inside should be white, using `Outfit SemiBold` (`labelLarge`).
- **Links**: In-text links or clickable text (like "Sign in") should be highlighted in the Primary Accent color and use `FontWeight.Bold` (or `SemiBold`).

## 4. Jetpack Compose Specifics

- Do not use XML layouts. All UI is written in Jetpack Compose using Material Design 3.
- Always use the predefined styles from `MaterialTheme.typography` and `MaterialTheme.colorScheme` instead of hardcoding colors and fonts in your `@Composable` functions.
- If a custom color is needed that isn't part of the standard Material color scheme (like `Neutral60`), reference it directly from the `Color.kt` constants, but try to limit exceptions to keep the design system cohesive.
