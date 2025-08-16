import '../Styles/attendance.css'
import attendanceImg1 from "../assets/Attendancepage_image1.jpg"; 
import attendanceImg2 from "../assets/Attendancepage _image2.jpg";

const Attendancepage = () =>{
    return(
        <div className="attendance-page">
      

      {/* Main Section */}
      <main className="main container">
        <div className="grid">
          {/* Left side */}
          <section>
            <img
              src={attendanceImg1}
              alt="Student raising hand"
              className="main-image1"
            />
            <div className="attendancepage_buttons">
              <button className="Att_page_button1">
               <span className="star">★</span> Add <span className="arrow">▼</span>
            </button>
              <button className="Att_page_button2">
               <span className="star">★</span> View <span className="arrow">▼</span>
            </button>
            </div>
          </section>

          {/* Right side */}
          <section>
        <div className='main_text1'>
            <h1>Never Miss a Beat <br></br>with Your <br></br>Attendance</h1>
            
        
            <h2>
              View your attendance percentage, <br></br>log presences, and maintain perfect<br></br>
              records
            </h2>
        </div>
            <img
            src={attendanceImg2}

              alt="Business growth graph"
              className="main-image2"
            />
          </section>
        </div>
      </main>
    </div>

    );
};
export default Attendancepage;