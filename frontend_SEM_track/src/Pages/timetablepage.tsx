import '../Styles/timetablepage.css'
const Timetablepage = () =>{
    return(
<div className="timetable-container2">
    <div className='image_text-container'>
      <div className="image-container">
        <img 
          src="/src/assets/Timetable_image.jpg" 
          alt="Timetable" 
          className="timetable-image"
        />
      </div>

      
        <div className="text-container">
          <h1>Effortlessly manage <br></br>your timetable:</h1>
          <p>
            Add, update, and delete <br></br>entries with just a few clicks.
          </p>
        </div>
   </div>

        <div className="button-container">
          <button className="button add">Add</button>
          <button className="button update">Update</button>
          <button className="button delete">Delete</button>
        </div>
        <div>
            <button className="button1">
               <span className="star">★</span> View <span className="arrow">▼</span>
            </button>
        </div>
</div>


    );
};
export default Timetablepage;