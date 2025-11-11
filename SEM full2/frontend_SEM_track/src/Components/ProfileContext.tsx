import { createContext, useContext, useState } from 'react';
import type { ReactNode,  } from 'react'; // Import types separately

interface ProfileData {
  fullName: string;
  role: string;
  phone: string;
  bio: string;
  studentId: string;
  major: string;
  dateOfBirth: string;
  avatarInitials: string;
}

interface ProfileContextType {
  profile: ProfileData | null;
  setProfile: (profile: ProfileData) => void;
  isProfileComplete: boolean;
}

const ProfileContext = createContext<ProfileContextType | undefined>(undefined);

export const useProfile = () => {
  const context = useContext(ProfileContext);
  if (context === undefined) {
    throw new Error('useProfile must be used within a ProfileProvider');
  }
  return context;
};

interface ProfileProviderProps {
  children: ReactNode;
}

export const ProfileProvider: React.FC<ProfileProviderProps> = ({ children }) => {
  const [profile, setProfile] = useState<ProfileData | null>(null);

  const isProfileComplete = !!profile;

  return (
    <ProfileContext.Provider value={{ profile, setProfile, isProfileComplete }}>
      {children}
    </ProfileContext.Provider>
  );
};