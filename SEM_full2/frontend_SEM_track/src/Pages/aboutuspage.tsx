import '../Styles/aboutus.css'
import teamImg from '../assets/aboutuspage_image.jpg';
const Aboutuspage = () => {
  return (
    <div className="about-us-container">
      <div className="content-wrapper">
        <div className="team-photo">
          <img src={teamImg} alt="Team" className="team-image" />
        </div>
        <div className="text-section">
          <h2>Welcome to Semester Sphere</h2>
          <p>
            The ultimate tool for tracking and managing your academic journey. We are a team of passionate developers
            dedicated to creating a user-friendly platform that helps students and academic staff efficiently monitor
            their progress throughout the semester.
          </p>
          <p>
            At Semester Sphere, our goal is to empower students with the tools they need to stay organized, track
            their schedules, manage assignments, view grades, and receive real-time notifications. With intuitive
            interfaces and smart notifications, we make managing your semester easier and more efficient.
          </p>
          <p>
            For educators and coordinators, our platform offers a seamless way to monitor student progress, manage
            schedules, and stay connected with students in a collaborative environment.
          </p>
          <p>
            We believe that the key to success is staying on top of your goals, and Semester Sphere is here to help you
            do just that!
          </p>
        </div>


      </div>
    </div>

  );
};

export default Aboutuspage;