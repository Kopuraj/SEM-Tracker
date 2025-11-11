import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import Header from "./Components/Header";
import Footer from "./Components/Footer";
import Homepage from "./Pages/homepage";
import Contactus from "./Pages/contactus";
import Aboutuspage from "./Pages/aboutuspage";
import Timetablepage from "./Pages/timetablepage";
import Attendancepage from "./Pages/attendancepage";
import ExamsQuizpage from "./Pages/examsquizpage";
import ProfilePage from "./Pages/ProfilePage";
import LoginPage from "./Pages/loginpage";
import Signuppage from "./Pages/signuppage"; // Add this import

function AppLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      <Header />
      <main style={{ minHeight: "calc(100vh - 120px)" }}>
        {children}
      </main>
      <Footer />
    </>
  );
}

function App() {
  return (
    <Router>
      <Routes>
        {/* Default route redirects to login */}
        <Route path="/" element={<Navigate to="/loginpage" replace />} />
        
        {/* Authentication routes (without layout) */}
        <Route path="/loginpage" element={<LoginPage />} />
        <Route path="/signuppage" element={<Signuppage />} />
        <Route path="/login" element={<Navigate to="/loginpage" replace />} /> {/* Alternative route */}
        <Route path="/signup" element={<Navigate to="/signuppage" replace />} /> {/* Alternative route */}

        {/* All protected routes with header/footer */}
        <Route
          path="/homepage"
          element={
            <AppLayout>
              <Homepage />
            </AppLayout>
          }
        />
        <Route
          path="/home" // Alternative route for homepage
          element={<Navigate to="/homepage" replace />}
        />
        <Route
          path="/contactus"
          element={
            <AppLayout>
              <Contactus />
            </AppLayout>
          }
        />
        <Route
          path="/aboutuspage"
          element={
            <AppLayout>
              <Aboutuspage />
            </AppLayout>
          }
        />
        <Route
          path="/timetablepage"
          element={
            <AppLayout>
              <Timetablepage />
            </AppLayout>
          }
        />
        <Route
          path="/attendancepage"
          element={
            <AppLayout>
              <Attendancepage />
            </AppLayout>
          }
        />
        <Route
          path="/examsquizpage"
          element={
            <AppLayout>
              <ExamsQuizpage />
            </AppLayout>
          }
        />
        <Route
          path="/profilepage"
          element={
            <AppLayout>
              <ProfilePage />
            </AppLayout>
          }
        />

        {/* Catch-all redirect */}
        <Route path="*" element={<Navigate to="/loginpage" replace />} />
      </Routes>
    </Router>
  );
}

export default App;