# Android Application Implementation Roadmap

With **Registration** and **Login** completed, your foundation is set. To complete the rest of the features listed in `docs/user-stories.md`, you should follow a **dependency-driven order**. You cannot build adoption applications without listings, and you cannot build foster/transporter flows without identity verification.

Here is the recommended phase-by-phase implementation plan using the **vertical slicing** approach.

---

## Phase 1: Identity & Authorization (The Prerequisites)
Before users can adopt, foster, transport, or act as a veterinarian, the system needs to know who they are and what they are allowed to do.

1. **Role Assignment**
   - **Why now:** Users need a dashboard UI to opt into roles (Foster, Transporter, Vet).
   - **Implementation:** A profile screen where users select their desired roles.
2. **Identity Verification (KYC)**
   - **Why now:** All advanced roles require approved KYC. 
   - **Implementation:** Document upload flow (Camera/Gallery) and waiting state UI until the Admin approves it.

---

## Phase 2: The Core Adoption Engine
Build the primary "happy path" of the platform: rehoming an animal.

3. **Adoption Listing (Owner)**
   - **Why now:** You need actual data in the database before you can build the browsing screens. Let owners post animals first.
   - **Implementation:** Multi-step form with photo upload, animal details, and form validation.
4. **Lifestyle Profile and Matching**
   - **Why now:** Adopters need to set their preferences before viewing animals to get tailored results.
   - **Implementation:** A quick questionnaire UI mapping to the matching engine.
5. **Browse and Search Adoption Listings**
   - **Why now:** Now that animals exist and profiles are set, users can browse.
   - **Implementation:** Feed UI with filtering (species, location, urgency).
6. **Submit and Track an Adoption Application**
   - **Why now:** The final step of the adoption flow.
   - **Implementation:** Application form and a status tracking screen (Pending, Accepted, Declined).

---

## Phase 3: Rescue, Triage & Field Operations
Now build the "emergency path". This requires coordination between all the different specialized roles you verified in Phase 1.

7. **Rescue Reporting**
   - **Why now:** Triggers the entire field operations workflow.
   - **Implementation:** Camera integration + automatic GPS location capture to report a stray/injured animal.
8. **Veterinarian: Review AI Photo Triage & Medical Guidance**
   - **Why now:** Once a rescue is reported, the AI runs triage, and Vets need to confirm or override it.
   - **Implementation:** Case review screen for Vets to override Gemini's urgency and post treatment advice.
9. **Foster: Case Acceptance & Transition**
   - **Why now:** Animals that are triaged need a temporary home.
   - **Implementation:** Case dashboard for Fosters to accept placements, update conditions, and eventually transition the case to an Adoption Listing.
10. **Transporter: Accept & Track Transport Tasks**
    - **Why now:** Moves the animal from the rescue site to the Foster/Vet.
    - **Implementation:** A live map or list of open tasks, with status updates (assigned, picked up, in transit, delivered).

---

## Phase 4: Cross-Cutting Communication & Engagement
These features enhance the flows built in Phases 2 and 3 and tie the platform together.

11. **Private In-App Messaging**
    - **Why now:** Fosters need to talk to Transporters; Adopters need to talk to Owners. It requires the context of an active case/application.
    - **Implementation:** Real-time chat screens using SignalR.
12. **Push Notifications**
    - **Why now:** You now have events (messages, geo-targeted rescue alerts, application updates) that need to wake up the app.
    - **Implementation:** Firebase Cloud Messaging (FCM) integration handling foreground and background payloads.
13. **Sponsor and Donor Functionalities**
    - **Why now:** Sponsors need existing cases and listings to pledge against.
    - **Implementation:** UI on the case/listing details screen to commit simulated financial pledges and track case progress.
14. **Reputation and Trust Badges**
    - **Why now:** Badges are awarded based on completed actions (completed transports, adoptions, verified reports). It is easiest to implement the UI for this once those actions actually exist.
    - **Implementation:** Profile UI updates to display points and trust badges.
