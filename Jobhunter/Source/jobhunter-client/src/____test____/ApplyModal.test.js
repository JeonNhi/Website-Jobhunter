import React from 'react';
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import ApplyModal from "../components/client/modal/apply.modal";
import { Provider } from "react-redux";
import { configureStore } from '@reduxjs/toolkit'; 
import { BrowserRouter as Router } from "react-router-dom";
import { callCreateResume, callUploadSingleFile } from "../config/api";

// Mô phỏng các API
jest.mock("../config/api", () => ({
  callCreateResume: jest.fn(),
  callUploadSingleFile: jest.fn(),
}));

// Hàm helper để setup component với state và props ban đầu
const setup = (initialState, props) => {
  const store = configureStore({
    reducer: {
      account: (state = initialState.account, action) => state,  
    },
  });

  return render(
    <Provider store={store}>
      <Router>
        <ApplyModal {...props} />
      </Router>
    </Provider>
  );
};

describe("ApplyModal Component", () => {
  it("UploadCV_01: Nộp CV thành công", async () => {
    const initialState = {
      account: { isAuthenticated: true, user: { email: "test@example.com", id: "123" } },
    };
    const props = {
      isModalOpen: true,
      setIsModalOpen: jest.fn(),
      jobDetail: { id: "job1", name: "Frontend Developer", company: { name: "TechCorp" } },
    };

    // Mô phỏng phản hồi từ API
    callCreateResume.mockResolvedValue({ data: true });

    setup(initialState, props);

    fireEvent.change(screen.getByLabelText("Upload file CV"), {
      target: { files: [new File(["(⌐□_□)"], "CV_Tester_Intern_HUN.pdf", { type: "application/pdf" })] },
    });

    fireEvent.click(screen.getByText("Rải CV Nào"));

    await waitFor(() => {
      expect(screen.getByText("Rải CV thành công!")).toBeInTheDocument();
    });
  });

  it("UploadCV_02: Nộp đơn khi chưa đăng nhập", () => {
    const initialState = {
      account: { isAuthenticated: false },
    };
    const props = {
      isModalOpen: true,
      setIsModalOpen: jest.fn(),
      jobDetail: { id: "job1", name: "Frontend Developer", company: { name: "TechCorp" } },
    };

    setup(initialState, props);

    fireEvent.click(screen.getByText("Apply Now"));

    expect(props.setIsModalOpen).toHaveBeenCalled();
    expect(window.location.href).toContain("/login");
  });

  it("UploadCV_03: Để trống Upload file CV", () => {
    const initialState = {
      account: { isAuthenticated: true, user: { email: "test@example.com" } },
    };
    const props = {
      isModalOpen: true,
      setIsModalOpen: jest.fn(),
      jobDetail: { id: "job1", name: "Frontend Developer", company: { name: "TechCorp" } },
    };

    setup(initialState, props);

    fireEvent.click(screen.getByText("Rải CV Nào"));

    expect(screen.getByText("Vui lòng upload file!")).toBeInTheDocument();
  });

  it("UploadCV_04: Nộp file lớn hơn 5MB", async () => {
    const initialState = {
      account: { isAuthenticated: true, user: { email: "test@example.com" } },
    };
    const props = {
      isModalOpen: true,
      setIsModalOpen: jest.fn(),
      jobDetail: { id: "job1", name: "Frontend Developer", company: { name: "TechCorp" } },
    };

    // Mô phỏng phản hồi khi upload file lớn hơn 5MB
    callUploadSingleFile.mockResolvedValue({ data: null, message: "File vượt quá 5MB" });

    setup(initialState, props);

    fireEvent.change(screen.getByLabelText("Upload file CV"), {
      target: {
        files: [new File(["".padStart(6 * 1024 * 1024)], "big_file.docx", { type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document" })],
      },
    });

    await waitFor(() => {
      expect(screen.getByText("Đã có lỗi xảy ra khi upload file.")).toBeInTheDocument();
    });
  });
});
