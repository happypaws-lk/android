# Auto-sliding Onboarding Screen

Enhance the `OnboardingScreen` to automatically cycle through slides with a smooth animation and looping behavior.

## User Review Required

> [!NOTE]
> The auto-slide timer will reset if the user manually swipes between pages. This ensures the user has enough time to read the content of a page they just navigated to.

## Proposed Changes

### UI Components

#### [MODIFY] [OnboardingScreen.kt](file:///C:/Users/Nethmina/happypaws-lk/android/app/src/main/java/lk/happypaws/app/ui/onboarding/OnboardingScreen.kt)

- Add a `LaunchedEffect` to handle the auto-slide timer and animation.
- Implement logic to calculate the next page index with looping support (returning to the first page after the last).
- Use `animateScrollToPage` with a custom animation spec for a smoother transition.
- Ensure the timer handles user interaction gracefully.

## Verification Plan

### Manual Verification
- Deploy the app and navigate to the Onboarding screen.
- Observe that the slides advance automatically every 5 seconds.
- Verify that the animation is smooth.
- Confirm that the pager loops back to the first slide after the last one.
- Manually swipe to a slide and verify that the auto-slide timer waits for the full duration before advancing again.
