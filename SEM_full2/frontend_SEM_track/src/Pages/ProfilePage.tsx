
import { useProfile } from '../Components/ProfileContext';
import ProfileSetup from '../Components/ProfileSetup';
import '../Styles/ProfilePage.css';

const ProfilePage = () => {
  const { profile, isProfileComplete } = useProfile();

  // If profile is not complete, show the setup form
  if (!isProfileComplete) {
    return <ProfileSetup />;
  }

  // Format date of birth for display
  const formatDate = (dateString: string) => {
    if (!dateString) return 'Not provided';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  return (
    <div className="profile-container">
      <div className="sidebar">
        <div className="sidebar-header">
          <h2>Settings</h2>
        </div>
        <ul className="sidebar-menu">
          <li className="active">Overview</li>
          <li>Settings</li>
          <li>Security</li>
          <li className="logout">Log Out</li>
        </ul>
      </div>
      
      <div className="profile-content">
        <div className="profile-header">
          <div className="avatar">
            <div className="avatar-placeholder">{profile?.avatarInitials}</div>
          </div>
          <div className="profile-info">
            <h1>{profile?.fullName}</h1>
            <p className="role">{profile?.role}</p>
            <p className="phone">{profile?.phone || 'No phone number provided'}</p>
          </div>
        </div>
        
        <div className="profile-section">
          <h3>BIOGRAPHY</h3>
          <p className="biography">
            {profile?.bio || 'No biography provided yet.'}
          </p>
        </div>
        
        <div className="profile-section">
          <h3>INFORMATION</h3>
          <div className="info-grid">
            <div className="info-item">
              <span className="info-label">Student ID</span>
              <span className="info-value">{profile?.studentId || 'Not provided'}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Major</span>
              <span className="info-value">{profile?.major || 'Not declared'}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Date of Birth</span>
              <span className="info-value">{formatDate(profile?.dateOfBirth || '')}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;