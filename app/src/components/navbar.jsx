import "../styles/navbar.css";

export default function Navbar() {
  return (
    <div>
      <nav>
        <img src="../assets/logo.png" alt="" />

        <ul>
          <li>
            <a href="/members">Members</a>
          </li>
          <li>
            <a href="/events">Events</a>
          </li>
          <li>
            <a href="/Blog">Blog</a>
          </li>
          <li>
            <a href="/contact">Contact</a>
          </li>
        </ul>
      </nav>
    </div>
  );
}
