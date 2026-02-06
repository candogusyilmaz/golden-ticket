import { useState } from "react";
import { generateGuid } from "../utils/generate-guid";
import { GoldenTicketSession } from "./golden-ticket-session";

export const GoldenTicketPage = () => {
  const [sessions, setSessions] = useState<string[]>([generateGuid()]);

  function addSession() {
    setSessions([...sessions, generateGuid()]);
  }

  function removeSession(id: string) {
    setSessions(sessions.filter((s) => s !== id));
  }

  return (
    <div className="app-wrapper">
      <div className="header">
        <h1>Golden Reserve</h1>
      </div>

      <div className="controls">
        <button className="add-btn" onClick={addSession}>
          + New Purchase Session
        </button>
      </div>

      <div className="sessions-grid">
        {sessions.map((id) => (
          <GoldenTicketSession
            key={id}
            sessionId={id}
            onClose={removeSession}
          />
        ))}
      </div>
    </div>
  );
};
