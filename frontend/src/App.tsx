import { Routes, Route, Navigate } from 'react-router-dom'
import MainLayout from './components/MainLayout'
import UserManagement from './pages/UserManagement'
import TeacherManagement from './pages/TeacherManagement'
import Login from './pages/Login'
import Profile from './pages/Profile'
import AuthGuard from './components/AuthGuard'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <AuthGuard>
            <MainLayout />
          </AuthGuard>
        }
      >
        <Route index element={<Navigate to="/users" replace />} />
        <Route path="users" element={<UserManagement />} />
        <Route path="teachers" element={<TeacherManagement />} />
        <Route path="profile" element={<Profile />} />
      </Route>
    </Routes>
  )
}

export default App
