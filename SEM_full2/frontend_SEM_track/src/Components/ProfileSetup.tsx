import React, { useState } from 'react';
import { useProfile } from '../Components/ProfileContext';
import '../Styles/ProfileSetup.css';

const ProfileSetup = () => {
  const { setProfile } = useProfile();
  const [formData, setFormData] = useState({
    fullName: '',
    role: 'Student',
    phone: '',
    bio: '',
    studentId: '',
    major: '',
    dateOfBirth: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // Generate avatar initials from full name
    const names = formData.fullName.split(' ');
    const avatarInitials = names.length >= 2 
      ? `${names[0][0]}${names[names.length - 1][0]}` 
      : formData.fullName.substring(0, 2);
    
    // Save profile data
    setProfile({
      ...formData,
      avatarInitials: avatarInitials.toUpperCase()
    });
  };

  return (
    <div className="profile-setup-container">
      <div className="profile-setup-box">
        <h2>Complete Your Profile</h2>
        <p className="setup-subtitle">Tell us a bit about yourself to get started</p>
        
        <form onSubmit={handleSubmit} className="profile-form">
          <div className="form-group">
            <label htmlFor="fullName">Full Name *</label>
            <input
              type="text"
              id="fullName"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="role">Role *</label>
            <select
              id="role"
              name="role"
              value={formData.role}
              onChange={handleChange}
              required
            >
              <option value="Student">Student</option>
              <option value="Teacher">Teacher</option>
              <option value="Alumni">Alumni</option>
              <option value="Staff">Staff</option>
            </select>
          </div>
          
          <div className="form-group">
            <label htmlFor="phone">Phone Number</label>
            <input
              type="tel"
              id="phone"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="studentId">Student ID</label>
            <input
              type="text"
              id="studentId"
              name="studentId"
              value={formData.studentId}
              onChange={handleChange}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="major">Major</label>
            <input
              type="text"
              id="major"
              name="major"
              value={formData.major}
              onChange={handleChange}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="dateOfBirth">Date of Birth</label>
            <input
              type="date"
              id="dateOfBirth"
              name="dateOfBirth"
              value={formData.dateOfBirth}
              onChange={handleChange}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="bio">Biography</label>
            <textarea
              id="bio"
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              rows={4}
              placeholder="Tell us about yourself, your interests, etc."
            />
          </div>
          
          <button type="submit" className="submit-btn">Save Profile</button>
        </form>
      </div>
    </div>
  );
};

export default ProfileSetup;