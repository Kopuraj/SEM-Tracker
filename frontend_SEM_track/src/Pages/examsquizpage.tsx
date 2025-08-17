import '../Styles/examsquizpage.css'
import examquizimage from "../assets/examsquizpage_image.jpg";
const  ExamsQuizpage= () =>{
    return(
        <div className="exam-container">
      {/* Header */}
      <header className="exam-header">
    
        <div className="search-box">
          <input
            type="text"
            placeholder="Search your exams"
            aria-label="Search exams"
          />
          <button aria-label="Search">
            <span className="material-icons">🔍</span>
          </button>
        </div>
      </header>

      {/* Main content */}
      <main className="exam-main">
        <h1>Add your quizzes & exam <br></br>details and see your progress</h1>
        <div className="exam-image">
          <img
             src={examquizimage}

            alt="Students taking exam"
          />
        </div>
      </main>

      {/* Action buttons */}
      <footer className="exam-footer">
        <button className="EQ_btn ">
                <span className="star">★</span> Add <span className="arrow">▼</span>
        </button>
        <button className="EQ_btn ">
                 <span className="star">★</span> View <span className="arrow">▼</span>
             
        </button>
      </footer>
    </div>

    );
};

export default ExamsQuizpage;
