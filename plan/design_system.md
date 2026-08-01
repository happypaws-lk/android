# HappyPaws UI/UX Design System (Android)

## 1. Global Aesthetic
HappyPaws Android follows a strict dark-mode-only, "quietly premium" utility aesthetic. It relies on Charcoal and Slate surfaces to build depth rather than shadows, using vibrant accents strictly for actionable or verified statuses.

## 2. Color Palette (Tokens)

All Compose colors will be defined in `theme/Color.kt` and applied via `MaterialTheme.colorScheme`.

| Token Name | Hex Code | Material 3 Mapping | Usage |
| :--- | :--- | :--- | :--- |
| **Vibrant Cyan** | `#4CE5E5` | `Primary` | Active states, primary buttons, "Verified" badges, glowing map pins. |
| **Neon Pink** | `#FF007F` | `Error` | Critical triage alerts, emergency actions, destructive actions. |
| **Deep Charcoal** | `#1E1E24` | `Background` | Base application background. Soft, non-fatiguing. |
| **Slate Grey** | `#2B2B36` | `Surface` | Cards, bottom sheets, form input backgrounds, bottom navigation. |
| **Dark Slate** | `#3A3A4A` | `Outline` | Borders, dividers, unselected input outlines. |
| **Pure White** | `#FFFFFF` | `OnBackground`, `OnSurface` | Primary text, headers, important data points. |
| **Cool Grey** | `#A0A0AB` | `OnSurfaceVariant` | Body text, captions, placeholders, inactive tabs. |

## 3. Typography Scale (Inter Font Family)

Font family `Inter` will be loaded in Compose.

- **Display Large**: 32sp, Bold (700)
- **Title Large (H1)**: 24sp, Bold (700)
- **Title Medium (H2)**: 18sp, Bold (700)
- **Body Large**: 16sp, Regular (400)
- **Body Medium (Paragraph)**: 14sp, Regular (400)
- **Label Large (Button)**: 14sp, Medium (500)
- **Label Small (Caption)**: 12sp, Regular (400)

## 4. Reusable Composables (`core/designsystem/components`)

### 4.1 Buttons
- **`HappyPawsPrimaryButton`**: Background `#4CE5E5`, Text `#1E1E24`, 8dp Corner Radius, Medium 14sp text.
- **`HappyPawsSecondaryButton`**: Transparent Background, Border `2dp solid #4CE5E5`, Text `#4CE5E5`, 8dp Corner Radius.
- **`HappyPawsUrgentButton`**: Background `#FF007F`, Text `#FFFFFF`. (Used only for emergency actions).

### 4.2 Form Inputs
- **`HappyPawsTextField`**: 
  - Resting: `#2B2B36` Background, `#3A3A4A` border, 8dp radius.
  - Focused: `2dp solid #4CE5E5` border.
  - Error: `1dp solid #FF007F` border, error text below.
- **`HappyPawsOtpInput`**: 5 individual rounded square boxes for OTP entry, snapping focus automatically.

### 4.3 Surfaces & Cards
- **`HappyPawsCard`**: Background `#2B2B36`, 12dp Corner Radius. No elevation shadow (elevation is implied by contrast against `#1E1E24`).
- **`HappyPawsStatusBadge`**: Small pill shape.
  - Verified: Background `#4CE5E5`, Text `#1E1E24`.
  - Critical: Background `#FF007F`, Text `#FFFFFF`.
  - Moderate: Outline `#FFFFFF`, Text `#FFFFFF`.
  - Low: No outline, Text `#A0A0AB`.

## 5. Micro-animations & Transitions
- **State Changes**: Simple `animateColorAsState` for button press effects (darken slightly on press).
- **List Insertions**: `animateItemPlacement()` inside `LazyColumn` for chats and feed items.
- **Loading State**: Shimmer effect on `#2B2B36` surfaces moving to a slightly lighter `#3A3A4A` gradient.
- **Map Pins**: `InfiniteTransition` for pulsing Neon Pink dots on the Rescue Radar.
