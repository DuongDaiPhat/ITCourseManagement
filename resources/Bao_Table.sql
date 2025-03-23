USE it_course_management;

-- Bảng MyCart
CREATE TABLE MyCart (
    UserID INT NOT NULL,
    CourseID INT NOT NULL,
    IsBuy BIT DEFAULT 0,
    AddedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (UserID, CourseID) 
);


-- Bảng Payments
CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    PaymentName NVARCHAR(255) NOT NULL
);


-- Bảng UserPayment
CREATE TABLE UserPayment (
    PaymentID INT,
    UserID INT,
    Balance FLOAT NOT NULL,
    PRIMARY KEY (PaymentID, UserID),
    FOREIGN KEY (PaymentID) REFERENCES Payments(PaymentID)
);


-- Bảng Invoices
CREATE TABLE Invoices (
    InvoiceID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT NOT NULL,
    TotalPrice FLOAT NOT NULL,
    BoughtAt DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- Bảng InvoiceDetail
CREATE TABLE InvoiceDetail (
    InvoiceID INT,
    CourseID INT,
    PRIMARY KEY (InvoiceID, CourseID),
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID)
);

-- Bảng Notification
CREATE TABLE Notification (
    NotificationID INT PRIMARY KEY AUTO_INCREMENT,
    NotificationName VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    Content VARCHAR(512) CHARACTER SET utf8mb4 NOT NULL,
    NotifiedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Bảng NotificationDetail
CREATE TABLE NotificationDetail (
    NotificationID INT,
    UserID INT,
    Status ENUM('read', 'unread') NOT NULL DEFAULT 'unread',
    PRIMARY KEY (NotificationID, UserID),
    FOREIGN KEY (NotificationID) REFERENCES Notification(NotificationID)
);
