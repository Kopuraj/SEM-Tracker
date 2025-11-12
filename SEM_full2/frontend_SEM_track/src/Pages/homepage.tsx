import '../Styles/homepage.css'

const Homepage = () =>{
    return (
    
    
      
    
        <main className="home-container">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-text">
          <div>
                  <h1>TRack Your Acadamic Journey with Ease</h1>
          </div>
          <div>
           <p>
            Our semester Progress Tracking System empowers students to manage their academic progress effortlessly.
            With features like attendance tracking, quiz reminders, and performance visualization, you can stay
            organized and focused on your goals.
          </p>
          <div className='hero-buttons'>
            <button className="btn">Get Started</button>
            <button className="btn">Learn More</button>
          </div>
          </div>
          
          
        </div>
        <div className="hero-image">
          <img src="src/assets/group-study.jpg" alt="Students studying" />
        </div>
      </section>

      {/* Features Section */}
      <section className="features">
        <h2>Stay on top of your academic journey with our powerful tracking features.</h2>
        <div className="feature-cards">
          <div className="card">
            <div className="icon">🕒</div>
            <h3>Effortlessly manage your schedule and never miss a class again.</h3>
            <p>
              Our attendance tracking feature ensures you're always present and accounted for.
            </p>
            
          </div>
          <div className="card">
            <div className="icon">📅</div>
            <h3>Keep track of your classes with our intuitive schedule viewer.</h3>
            <p>
              View your daily class schedules at a glance to optimize your time.
            </p>
          </div>
          <div className="card">
            <div className="icon">🔔</div>
            <h3>Prepare effectively with alerts for upcoming quizzes and exams.</h3>
            <p>
              Stay ahead of deadlines by monitoring your assessments with ease.
            </p>
           
          </div>
        </div>
      </section>

      {/* Join Section */}
      <section className='join-us'>
        
        <div className="join-content">
         
      
          <h1>Join us Today to make your semester effectively !</h1>
        
          
        <div className="join-image">
          <img src="/src/assets/homepage_image2.jpg" alt="Calendar scheduling" />
        </div>
          
        </div>
       
        
      </section>
    </main>
    );
};
export default Homepage;