import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { ProfileProvider } from './Components/ProfileContext'; // ✅ import your provider

createRoot(document.getElementById('root')!).render(
  <StrictMode>

<ProfileProvider>
      <App />
    </ProfileProvider>
  </StrictMode>,
)
