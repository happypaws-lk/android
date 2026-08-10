# Implementation Plan - Fix Email Keyboard Type and Focus Flow

Fix the issue where focusing on the email field triggers voice typing instead of the standard keyboard, and improve the form navigation flow.

## User Review Required

> [!NOTE]
> I will be updating the `KeyboardOptions` to include explicit `ImeAction`s. This helps the Android system understand the relationship between the fields and usually prevents it from defaulting to "unusual" input methods like voice typing.

## Proposed Changes

### UI Layer

#### [MODIFY] [LoginScreen.kt](file:///C:/Users/Nethmina/happypaws-lk/android/app/src/main/java/lk/happypaws/app/ui/auth/LoginScreen.kt)
- Update Email `OutlinedTextField`:
    - Add `imeAction = ImeAction.Next` to `KeyboardOptions`.
    - Add `keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })`.
- Update Password `OutlinedTextField`:
    - Add `imeAction = ImeAction.Done` to `KeyboardOptions`.
    - Add `keyboardActions = KeyboardActions(onDone = { viewModel.login(email, password, context) })`.
- Use `LocalFocusManager.current` to manage focus transitions.
- Temporarily change `KeyboardType.Email` to `KeyboardType.Text` if the issue persists, but I'll start by just adding the `ImeAction`.

## Verification Plan

### Manual Verification
1. **Focus Email**: Tap the Email field. Verify that the standard QWERTY keyboard appears instead of the voice typing bar.
2. **Keyboard Navigation**: Press the "Next" button on the keyboard while in the Email field. Verify focus moves to the Password field.
3. **Form Submission**: Press the "Done" button on the keyboard while in the Password field. Verify it triggers the login action.
